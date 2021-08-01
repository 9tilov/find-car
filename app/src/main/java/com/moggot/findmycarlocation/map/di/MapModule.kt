package com.moggot.findmycarlocation.map.di

import com.moggot.findmycarlocation.base.db.AppDatabase
import com.moggot.findmycarlocation.data.api.LocationApi
import com.moggot.findmycarlocation.map.data.MapDataRepo
import com.moggot.findmycarlocation.map.domain.MapInteractor
import com.moggot.findmycarlocation.map.domain.MapInteractorImpl
import com.moggot.findmycarlocation.map.domain.MapRepo
import com.moggot.findmycarlocation.parking.data.local.ParkingSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
class MapModule {

    @Provides
    @ActivityRetainedScoped
    fun provideMapInteractor(mapRepo: MapRepo): MapInteractor = MapInteractorImpl(mapRepo)

    @Provides
    @ActivityRetainedScoped
    fun provideMapRepo(retrofit: Retrofit, parkingSource: ParkingSource): MapRepo =
        MapDataRepo(retrofit.create(LocationApi::class.java), parkingSource)
}
