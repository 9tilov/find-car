package com.moggot.findmycarlocation;

import android.*;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MapActivity extends NetworkActivity implements OnMapReadyCallback, NetworkActivity.LocationObserver {

    final static String LOG_TAG = "myLogs";
    private BroadcastReceiver receiver;

    private Map mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.screen_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Ad advertisment = new Ad(this);
        advertisment.ShowBanner(R.id.adViewMap);
        advertisment.ShowInterstitial(R.string.banner_ad_unit_id_map_interstitial);

        registerLocationObserver(this);
        initLocationServices();

        IntentFilter filter = new IntentFilter(Consts.PACKAGE_NAME);
        receiver = new ProximityIntentReceiver();
        registerReceiver(receiver, filter);

        FirebaseAnalytics mFirebaseAnalytics = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED)
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Consts.FIREBASE_ITEM_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, Consts.FIREBASE_ITEM_NAME);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Consts.FIREBASE_CONTENT_TYPE);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = new Map(this, map);
        checkLocationSettings();
    }

    @Override
    public void onScanLocationStarted(final NetworkActivity activity) {
        Log.i(LOG_TAG, "MaponScanLocationStarted");
    }

    @Override
    public void onScanLocationFinished(final NetworkActivity activity) {
        Location location = getLocation();
        mMap.setUpMap(location);
        Log.i(LOG_TAG, "MaponScanLocationFinished");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterLocationObserver(this);
    }

}