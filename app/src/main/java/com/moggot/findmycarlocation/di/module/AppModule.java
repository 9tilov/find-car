package com.moggot.findmycarlocation.di.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Context appContext;

    public AppModule(Context context) {
        appContext = context.getApplicationContext();
    }

    @Provides
    @Singleton
    Context provideContext() {
        return appContext;
    }
}
