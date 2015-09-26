package com.moggot.findmycarlocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dmitry on 12.09.15.
 */
public class NetworkManager {

    private Context ctx;
    final static String LOG_TAG = "myLogs";
    private static Location mLocation = null;

    public LocationManager locationManager;

    public NetworkManager(Context context) {
        this.ctx = context;
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        Log.d(LOG_TAG, "constructor NetworkManager");

    }

    public LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            Log.d(LOG_TAG, "LOCATION ENABLE!!!!");
            mLocation = location;
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(LOG_TAG, "onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Log.d(LOG_TAG, "Status_gps1: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                Log.d(LOG_TAG, "Status_network1: " + String.valueOf(status));
            }
        }
    };

    public Location _getLocation() {

        Log.d(LOG_TAG, "isSimSupport: " + isSimSupport());if (mLocation == null) {
            Log.d(LOG_TAG, "mLocation == null: ");
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria,
                    false);
            if (ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                mLocation = locationManager
                        .getLastKnownLocation(provider);
        }
        if (isSimSupport()) {
            if (isNetworkEnable()) {

                if (mLocation != null) {
                    return mLocation;
                } else {
                    turn_on_wifi();
                    ((Activity) ctx).startActivityForResult(new Intent(
                            android.provider.Settings.ACTION_WIFI_SETTINGS), SharedPreference.ACTIVITY_RESULT_CODE.WIFI_SETTINGS);
                }
            } else {
                ((Activity) ctx).startActivityForResult(new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), SharedPreference.ACTIVITY_RESULT_CODE.LOCATION_SETTINGS);
            }
        } else {
            Log.d(LOG_TAG, "isWifiEnable: " + isWifiEnable());
            if (isWifiEnable()) {
                if (!isNetworkEnable()) {
                    ((Activity) ctx).startActivityForResult(new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), SharedPreference.ACTIVITY_RESULT_CODE.LOCATION_SETTINGS);
                }
                Log.d(LOG_TAG, "mLocation = " + mLocation);
                if (mLocation != null) {
                    return mLocation;
                } else
                    no_internet();
            } else {
                ((Activity) ctx).startActivityForResult(new Intent(
                        android.provider.Settings.ACTION_WIFI_SETTINGS), SharedPreference.ACTIVITY_RESULT_CODE.WIFI_SETTINGS);
            }
        }
        return null;
    }

    public boolean isSimSupport() {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
    }

    public boolean isWifiEnable() {
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
    }

    public boolean isNetworkEnable() {
        boolean isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isNetworkEnable;
    }

    private void turn_on_wifi() {
        Toast.makeText(ctx, R.string.turn_on_wifi,
                Toast.LENGTH_SHORT).show();
    }

    private void no_internet() {
        Toast.makeText(ctx, R.string.no_internet,
                Toast.LENGTH_SHORT).show();
    }

}
