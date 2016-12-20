package com.moggot.findmycarlocation;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

/**
 * Created by dmitry on 23.11.16.
 */

class Map {

    private static final String LOG_TAG = "Map";

    private GoogleMap mGoogleMap;
    private Context mCtx;
    private LocationManager mLocationManager;

    private boolean isPathBuild = false;

    final ArrayList<LatLng> mMarkers = new ArrayList<>();

    Map(Context ctx, GoogleMap map) {
        mCtx = ctx;
        mGoogleMap = map;
        if (ActivityCompat.checkSelfPermission(mCtx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mCtx, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationManager = (LocationManager) mCtx.getSystemService(Context.LOCATION_SERVICE);
    }

    void setUpMap(Location location) {

        if (isPathBuild)
            return;

        if (!isInternetEnable()) {
            no_internet();
            return;
        }

        LatLng arrivalPoint = SharedPreference.LoadLocation(mCtx);
        LatLng departurePoint = new LatLng(location.getLatitude(),
                location.getLongitude());

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(departurePoint, 15.0f));
        mMarkers.add(departurePoint);
        MarkerOptions departureOptions = new MarkerOptions();
        departureOptions.position(departurePoint);
        departureOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMarkers.add(arrivalPoint);
        MarkerOptions arrivalOptions = new MarkerOptions();
        arrivalOptions.position(arrivalPoint);
        arrivalOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
        drawMarker(departurePoint, arrivalPoint);
        drawCircle(arrivalPoint);

        if (mMarkers.size() == 1) {
            no_points();
            return;
        }

        buildPath(departurePoint, arrivalPoint);
    }

    private void buildPath(LatLng departurePoint, LatLng arrivalPoint) {
        if (mMarkers.size() < 2)
            return;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(departurePoint);
        builder.include(arrivalPoint);

        LatLng origin = mMarkers.get(0);
        LatLng dest = mMarkers.get(1);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

        Intent intent = new Intent(Consts.PACKAGE_NAME);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(mCtx, 0, intent, 0);
        if (ActivityCompat.checkSelfPermission(mCtx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mCtx, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationManager.addProximityAlert(arrivalPoint.latitude,
                    arrivalPoint.longitude, 5, -1, proximityIntent);
        isPathBuild = true;
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

            StringBuilder sb = new StringBuilder();

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

            if (points != null) {
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

                final String sDistance = mCtx.getString(R.string.distance) + " " + distance;
                final String sDuration = mCtx.getString(R.string.duration) + " " + duration;
                TextView tvDistance = (TextView) ((Activity) mCtx).findViewById(R.id.tv_distance_time);
                TextView tvDuration = (TextView) ((Activity) mCtx).findViewById(R.id.tv_duration_time);
                tvDistance.setText(sDistance);
                tvDuration.setText(sDuration);
                mGoogleMap.addPolyline(lineOptions);
            }

        }
    }

    private void drawMarker(LatLng departurePoint, LatLng arrivalPoint) {
        // Creating an instance of MarkerOptions
        MarkerOptions departureMarkerOptions = new MarkerOptions();
        MarkerOptions arrivalOptions = new MarkerOptions();
        // Setting latitude and longitude for the marker
        departureMarkerOptions.position(departurePoint);
        arrivalOptions.position(arrivalPoint);

        mGoogleMap.addMarker(departureMarkerOptions.icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.man)));

        mGoogleMap.addMarker(arrivalOptions.icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.car)));
    }

    private void drawCircle(LatLng point) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(10);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mGoogleMap.addCircle(circleOptions);

    }

    private boolean isInternetEnable() {
        ConnectivityManager cm =
                (ConnectivityManager) mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void no_points() {
        Toast.makeText(mCtx, R.string.no_points, Toast.LENGTH_SHORT)
                .show();
    }

    private void no_internet() {
        Toast.makeText(mCtx, R.string.no_internet, Toast.LENGTH_SHORT).show();
    }
}
