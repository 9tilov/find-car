package com.moggot.findmycarlocation.di.module;

import com.moggot.findmycarlocation.MainActivity;
import com.moggot.findmycarlocation.about.PrivacyPolicyActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract PrivacyPolicyActivity contributePrivacyPolicyActivity();
}
