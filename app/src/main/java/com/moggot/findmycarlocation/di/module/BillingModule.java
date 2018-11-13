package com.moggot.findmycarlocation.di.module;

import android.content.Context;

import com.moggot.findmycarlocation.billing.BillingManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BillingModule {

    @Provides
    @Singleton
    BillingManager provideBillingManager(Context context) {
        return new BillingManager(context);
    }
}
