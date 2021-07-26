package com.moggot.findmycarlocation

import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class App : MultiDexApplication() {

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