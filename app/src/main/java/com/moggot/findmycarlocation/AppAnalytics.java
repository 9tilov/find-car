package com.moggot.findmycarlocation;

import android.app.Activity;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

public class AppAnalytics {

    private static final String PROPERTY_ID = "UA-66799500-6";

    private FirebaseAnalytics firebaseAnalytics;
    private final Map<TrackerName, Tracker> trackers = new HashMap<>();
    private Activity activity;

    public AppAnalytics(Activity activity) {
        this.activity = activity;
        firebaseAnalytics = FirebaseAnalytics.getInstance(activity);
    }

    public void setCurrentScreen(String tag) {
        firebaseAnalytics.setCurrentScreen(activity, tag, null);
        Tracker t = getTracker(TrackerName.APP_TRACKER);
        t.setScreenName(tag);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private Tracker getTracker(TrackerName trackerId) {
        synchronized (this) {
            if (!trackers.containsKey(trackerId)) {

                GoogleAnalytics analytics = GoogleAnalytics.getInstance(activity);
                Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
                        .newTracker(R.xml.app_tracker) : analytics
                        .newTracker(PROPERTY_ID);
                t.enableAdvertisingIdCollection(true);
                trackers.put(trackerId, t);

            }
            return trackers.get(trackerId);
        }
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }
}
