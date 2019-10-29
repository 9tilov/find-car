package com.moggot.findmycarlocation

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.MobileAds
import com.moggot.findmycarlocation.di.ApplicationProvider
import com.moggot.findmycarlocation.di.ComponentHolder
import com.moggot.findmycarlocation.di.component.AppComponent
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ComponentHolder.provideComponent(ApplicationProvider::class.java.name) {
            AppComponent.Initializer.init(this)
        }
        MobileAds.initialize(this, getString(R.string.app_id))
        LeakCanary.install(this)
        Fabric.with(this, Crashlytics())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        if (BuildConfig.DEBUG) {
            MultiDex.install(this)
        }
    }
}