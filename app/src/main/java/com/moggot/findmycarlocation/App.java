package com.moggot.findmycarlocation;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.moggot.findmycarlocation.di.component.AppComponent;
import com.moggot.findmycarlocation.di.component.DaggerAppComponent;
import com.moggot.findmycarlocation.di.module.AppModule;
import com.squareup.leakcanary.LeakCanary;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class App extends Application {

    private static final String PROPERTY_ID = "UA-66799500-6";
    private static App instance;
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    private AppComponent appComponent;

    public static App getInstance() {
        return instance;
    }

    private static void setInstance(App instance) {
        App.instance = instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAnalysis firebase = new FirebaseAnalysis(this);
        firebase.init();
        MobileAds.initialize(this, getString(R.string.app_id));
        setInstance(this);
        this.appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Fabric.with(this, new Crashlytics());
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
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

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }
}