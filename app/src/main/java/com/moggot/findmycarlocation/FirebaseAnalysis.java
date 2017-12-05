package com.moggot.findmycarlocation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by dmitry on 25.11.16.
 */

public class FirebaseAnalysis {

    private final static String FIREBASE_ITEM_ID = "id";
    private final static String FIREBASE_ITEM_NAME = "name";
    private final static String FIREBASE_CONTENT_TYPE = "image";

    private FirebaseAnalytics mFirebaseAnalytics;

    public FirebaseAnalysis(Context ctx) {
        if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED)
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
    }

    public void init() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, FIREBASE_ITEM_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, FIREBASE_ITEM_NAME);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, FIREBASE_CONTENT_TYPE);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
