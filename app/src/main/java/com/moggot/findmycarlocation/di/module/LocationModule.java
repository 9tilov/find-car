package com.moggot.findmycarlocation.di.module;

import android.content.Context;

import com.google.android.gms.location.LocationRequest;
import com.patloew.rxlocation.RxLocation;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationModule {

    @Provides
    @Singleton
    LocationRequest provideLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000);
    }

    @Provides
    @Singleton
    RxLocation provideRxLocation(Context context) {
        RxLocation rxLocation = new RxLocation(context);
        rxLocation.setDefaultTimeout(15, TimeUnit.SECONDS);
        return rxLocation;
    }
}
