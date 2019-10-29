package com.moggot.findmycarlocation.di.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.moggot.findmycarlocation.data.repository.local.LocalRepo;
import com.moggot.findmycarlocation.data.repository.local.SettingsPreferences;
import com.moggot.findmycarlocation.di.scope.MainScope;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @MainScope
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences("storage", Context.MODE_PRIVATE);
    }

    @Provides
    @MainScope
    SettingsPreferences providePreferences(SharedPreferences sharedPreferences) {
        return new SettingsPreferences(sharedPreferences);
    }

    @Provides
    @MainScope
    LocalRepo provideLocalRepo(SettingsPreferences settingsPreferences) {
        return new LocalRepo(settingsPreferences);
    }
}
