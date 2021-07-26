package com.moggot.findmycarlocation.di

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.moggot.findmycarlocation.data.api.LocationApi
import com.moggot.findmycarlocation.data.repository.local.LocalRepo
import com.moggot.findmycarlocation.data.repository.network.NetworkRepo
import com.moggot.findmycarlocation.retry.RetryManager
import com.patloew.rxlocation.RxLocation

interface ApplicationProvider {

    fun provideContext(): Context
}
