package com.moggot.findmycar;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.model.PolylineOptions;

public class ScreenMap extends TrackedActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private BroadcastReceiver reciever;
    final static String LOG_TAG = "myLogs";
    TextView tvDistance, tvDuration;

    public enum locationType {
        USER_LOCATION, CAR_LOCATION
    }

    final String PROX_ALERT_INTENT = "com.example.findmycar";

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_map);


        tvDistance = (TextView) findViewById(R.id.tv_distance_time);
        tvDuration = (TextView) findViewById(R.id.tv_duration_time);
        final ArrayList<LatLng> markerPoints;
        Typeface font = Typeface.createFromAsset(getAssets(), "Dashley.ttf");
        tvDistance.setTypeface(font);
        tvDuration.setTypeface(font);
        Button btnFindCar = (Button) findViewById(R.id.buttonFindCar);
        SemiCircleDrawable dr_stop = new SemiCircleDrawable(
                Color.parseColor("#CFCFCF"));
        btnFindCar.setBackgroundDrawable(dr_stop);
        btnFindCar.setTypeface(font);
        btnFindCar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.SaveIsLocationSavedState(
                        getApplicationContext(), false);
                finish();
            }
        });

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        final ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, false);

        mMap = fm.getMap();

        mMap.setTrafficEnabled(true);
        mMap.setMyLocationEnabled(true);
        if (provider != null && !provider.equals("")) {

            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {

                LatLng departurePoint = new LatLng(location.getLatitude(),
                        location.getLongitude());
                LatLng arrivalPoint = SharedPreference.LoadLocation(this);
                markerPoints.add(departurePoint);
                MarkerOptions departureOptions = new MarkerOptions();
                departureOptions.position(departurePoint);
                departureOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                drawMarker(departurePoint, locationType.USER_LOCATION);
                markerPoints.add(arrivalPoint);
                MarkerOptions arrivalOptions = new MarkerOptions();
                arrivalOptions.position(arrivalPoint);
                arrivalOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                drawMarker(arrivalPoint, locationType.CAR_LOCATION);
                drawCircle(arrivalPoint);

                if (markerPoints.size() == 1) {
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                            departurePoint, 12);
                    mMap.moveCamera(update);
                } else if (markerPoints.size() == 2) {
                    Log.d(LOG_TAG, "markerPoints");
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(departurePoint);
                    builder.include(arrivalPoint);
                    LatLngBounds bounds = builder.build();

                    final DisplayMetrics display = getResources()
                            .getDisplayMetrics();
                    int width = display.widthPixels;
                    int height = display.heightPixels;

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(
                            bounds, width - (int) (width * 0.2), height
                                    - (int) (height * 0.4), 0);
                    mMap.moveCamera(cu);
                    mMap.animateCamera(cu);

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
                    locationManager.addProximityAlert(arrivalPoint.latitude,
                            arrivalPoint.longitude, 2, -1, proximityIntent);

                    IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
                    reciever = new ProximityIntentReceiver();
                    registerReceiver(reciever, filter);
                    Log.d(LOG_TAG, "markerPoints_end");
                }
            } else {
                no_location();
                startActivity(new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

        } else {
            no_provider();
            startActivity(new Intent(
                    android.provider.Settings.ACTION_WIFI_SETTINGS));
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
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
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
                Log.d("Background Task", e.toString());
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
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";
            // Traversing through all the routes
            if (!isNetworkAvailable()) {
                no_internet();
                startActivity(new Intent(
                        android.provider.Settings.ACTION_WIFI_SETTINGS));
                return;
            }
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) { // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);

                // Changing the color polyline according to the mode
                lineOptions.color(Color.BLUE);

            }

            if (result.size() < 1) {
                no_points();
                return;
            }
            Log.d(LOG_TAG, "distance = " + distance);
            Log.d(LOG_TAG, "duration = " + duration);

            tvDistance.setText(getResources().getString(R.string.distance)
                    + " " + distance);
            tvDuration.setText(getResources().getString(R.string.duration)
                    + " " + duration);
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Log.d(LOG_TAG, "Status_gps: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                Log.d(LOG_TAG, "Status_provider: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            Log.d(LOG_TAG, "Status_gps_location: " + formatLocation(location));
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            Log.d(LOG_TAG, "Status_provider_location: "
                    + formatLocation(location));
        }
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (reciever != null) {
            unregisterReceiver(reciever);
            reciever = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return true;
        }
        if (id == R.id.info) {
            Intent intent = new Intent(ScreenMap.this, ScreenInfo.class);
            startActivityForResult(intent, 3);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void drawMarker(LatLng point, locationType type) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Adding marker on the Google Map
        if (type == locationType.CAR_LOCATION)
            mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(R.mipmap.car)));
        else
            mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(R.mipmap.man)));
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


    private void no_location() {
        Toast.makeText(getBaseContext(), R.string.no_location,
                Toast.LENGTH_SHORT).show();
    }

    private void no_provider() {
        Toast.makeText(getBaseContext(), R.string.no_provider,
                Toast.LENGTH_SHORT).show();
    }

    private void no_internet() {
        Toast.makeText(getBaseContext(), R.string.no_internet,
                Toast.LENGTH_SHORT).show();
    }

    private void no_points() {
        Toast.makeText(getBaseContext(), R.string.no_points, Toast.LENGTH_SHORT)
                .show();
    }
}
