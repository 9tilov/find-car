package com.moggot.findmycarlocation.di.module;

import android.content.Context;

import com.moggot.findmycarlocation.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(App application) {
        return application.getApplicationContext();
    }
}
