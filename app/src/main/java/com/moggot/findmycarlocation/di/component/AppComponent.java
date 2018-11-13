package com.moggot.findmycarlocation.di.component;

import com.moggot.findmycarlocation.App;
import com.moggot.findmycarlocation.di.module.ActivityModule;
import com.moggot.findmycarlocation.di.module.AppModule;
import com.moggot.findmycarlocation.di.module.BillingModule;
import com.moggot.findmycarlocation.di.module.DataModule;
import com.moggot.findmycarlocation.di.module.FragmentModule;
import com.moggot.findmycarlocation.di.module.LocationModule;
import com.moggot.findmycarlocation.di.module.NetworkModule;
import com.moggot.findmycarlocation.di.module.ViewModelModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {AppModule.class,
        DataModule.class,
        LocationModule.class,
        NetworkModule.class,
        FragmentModule.class,
        ActivityModule.class,
        ViewModelModule.class,
        BillingModule.class})
interface AppComponent extends AndroidInjector<App> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<App> {
    }
}