package com.moggot.findmycarlocation;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScreenMap extends TrackedActivity implements OnMapReadyCallback {

    GoogleMap mMap = null;
    private BroadcastReceiver receiver;
    final static String LOG_TAG = "myLogs";
    Location mCurrentLocation;
    NetworkManager nwM;

    final String PROX_ALERT_INTENT = "com.example.findmycar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.screen_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!isInternetEnable()) {
            Log.i(LOG_TAG, "no_internet");
            no_internet();
        }

        Ad advertisment = new Ad(this);
        advertisment.ShowBanner(R.id.adViewMap);
        advertisment.ShowInterstitial(R.string.banner_ad_unit_id_map_interstitial);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Log.i(LOG_TAG, "3 = " + mMap);
        setUpMap();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private void setUpMap() {
        final ArrayList<LatLng> markerPoints = new ArrayList<>();

        LatLng arrivalPoint = SharedPreference.LoadLocation(this);
//        LatLng arrivalPoint = new LatLng(55.9408549, 37.5223485);
        nwM = new NetworkManager(this);

        getLocation();

        if (mCurrentLocation == null) {
            if (isGPSenable())
                no_location();
            return;
        }

        LatLng departurePoint = new LatLng(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude());

        Log.i(LOG_TAG, "departurePoint = " + departurePoint);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(departurePoint, 13));
        markerPoints.add(departurePoint);
        MarkerOptions departureOptions = new MarkerOptions();
        departureOptions.position(departurePoint);
        departureOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        markerPoints.add(arrivalPoint);
        MarkerOptions arrivalOptions = new MarkerOptions();
        arrivalOptions.position(arrivalPoint);
        arrivalOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
        drawMarker(departurePoint, arrivalPoint);
        drawCircle(arrivalPoint);

        if (markerPoints.size() == 1) {
            no_points();
            return;
        }
        if (markerPoints.size() == 2) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(departurePoint);
            builder.include(arrivalPoint);

            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

            Intent intent = new Intent(PROX_ALERT_INTENT);
            PendingIntent proximityIntent = PendingIntent.getBroadcast(
                    this, 0, intent, 0);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                nwM.locationManager.addProximityAlert(arrivalPoint.latitude,
                        arrivalPoint.longitude, 2, -1, proximityIntent);

            IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
            receiver = new ProximityIntentReceiver();
            registerReceiver(receiver, filter);
        }

    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(LOG_TAG, "mCurrentLocation1 = " + mCurrentLocation);
            mCurrentLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void getLocation() {
        // The minimum distance to change Updates in meters
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSEnabled) {
                nwM.checkLocationSettings();
                return;
            }

            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                Log.d("Network", "Network");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    mCurrentLocation = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            // if GPS Enabled get lat/long using GPS Services
            if (mCurrentLocation == null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                Log.d("GPS Enabled", "GPS Enabled");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    mCurrentLocation = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "mCurrentLocation111 = " + mCurrentLocation);
        if (mCurrentLocation == null) {
            nwM.checkLocationSettings();
            return;
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
                + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
        } finally {
            if (iStream != null)
                iStream.close();
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            String distance = "";
            String duration = "";
            if (result == null)
                return;
            ArrayList<LatLng> points = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();


                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) { // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
            }

            PolylineOptions lineOptions = new PolylineOptions();
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(4);

            // Changing the color polyline according to the mode
            lineOptions.color(Color.BLUE);

            if (result.size() < 1) {
                no_points();
                return;
            }

            final String sDistance = getResources().getString(R.string.distance) + " " + distance;
            final String sDuration = getResources().getString(R.string.duration) + " " + duration;
            TextView tvDistance = (TextView) findViewById(R.id.tv_distance_time);
            TextView tvDuration = (TextView) findViewById(R.id.tv_duration_time);
            tvDistance.setText(sDistance);
            tvDuration.setText(sDuration);
            mMap.addPolyline(lineOptions);

        }
    }

    private boolean isGPSenable() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isInternetEnable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void drawMarker(LatLng departurePoint, LatLng arrivalPoint) {
        // Creating an instance of MarkerOptions
        MarkerOptions departureMarkerOptions = new MarkerOptions();
        MarkerOptions arrivalOptions = new MarkerOptions();
        // Setting latitude and longitude for the marker
        departureMarkerOptions.position(departurePoint);
        arrivalOptions.position(arrivalPoint);

        mMap.addMarker(departureMarkerOptions.icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.man)));

        mMap.addMarker(arrivalOptions.icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.car)));
    }

    private void drawCircle(LatLng point) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(2);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }

    private void no_points() {
        Toast.makeText(getBaseContext(), R.string.no_points, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int REQUEST_CHECK_SETTINGS = 199;
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(LOG_TAG, "User agreed to make required location settings changes.");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setUpMap();
                            }
                        }, 3000);
                        break;

                    case Activity.RESULT_CANCELED:
                        Log.i(LOG_TAG, "User chose not to make required location settings changes.");
                        break;
                }
        }

    }

    private void no_location() {
        Toast.makeText(this, R.string.no_location, Toast.LENGTH_SHORT).show();
    }

    private void no_internet() {
        Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
    }

}