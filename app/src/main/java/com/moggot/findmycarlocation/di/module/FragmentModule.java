package com.moggot.findmycarlocation.di.module;

import com.moggot.findmycarlocation.about.AboutFragment;
import com.moggot.findmycarlocation.home.HomeFragment;
import com.moggot.findmycarlocation.map.GoogleMapFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Philippe on 02/03/2018.
 */

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract GoogleMapFragment contributeMapFragment();

    @ContributesAndroidInjector
    abstract AboutFragment contibuteAboutFragment();
}
