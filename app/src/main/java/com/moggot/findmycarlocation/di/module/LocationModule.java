package com.moggot.findmycarlocation.di.module;

import android.content.Context;

import com.google.android.gms.location.LocationRequest;
import com.moggot.findmycarlocation.di.scope.MainScope;
import com.patloew.rxlocation.RxLocation;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationModule {

    @Provides
    @MainScope
    LocationRequest provideLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000);
    }

    @Provides
    @MainScope
    RxLocation provideRxLocation(Context context) {
        RxLocation rxLocation = new RxLocation(context);
        rxLocation.setDefaultTimeout(15, TimeUnit.SECONDS);
        return rxLocation;
    }
}
