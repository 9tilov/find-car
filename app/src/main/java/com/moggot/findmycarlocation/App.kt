package com.moggot.findmycarlocation

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    companion object {
        const val TAG = "[FIND_CAR_LOCATION]"
    }
}