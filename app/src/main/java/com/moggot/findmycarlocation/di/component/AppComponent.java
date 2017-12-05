package com.moggot.findmycarlocation.di.component;

import com.moggot.findmycarlocation.di.module.AppModule;
import com.moggot.findmycarlocation.di.module.DataModule;
import com.moggot.findmycarlocation.di.module.LocationModule;
import com.moggot.findmycarlocation.di.module.NetworkModule;
import com.moggot.findmycarlocation.presentation.main.MainActivity;
import com.moggot.findmycarlocation.presentation.map.MapActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, DataModule.class, LocationModule.class, NetworkModule.class})
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(MapActivity mapActivity);
}
