package com.moggot.findmycarlocation;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.leakcanary.LeakCanary;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

/**
 * This is a subclass of {@link Application} used to provide shared objects for
 * this app, such as the {@link Tracker}.
 */
public class AnalyticsApplication extends Application {
    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-66799500-6";

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Fabric.with(this, new Crashlytics());
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
                    .newTracker(R.xml.app_tracker) : analytics
                    .newTracker(PROPERTY_ID);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}