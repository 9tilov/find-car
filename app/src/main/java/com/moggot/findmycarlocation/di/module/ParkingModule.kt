package com.moggot.findmycarlocation.di.module

import com.moggot.findmycarlocation.base.db.AppDatabase
import com.moggot.findmycarlocation.parking.data.ParkingDataRepo
import com.moggot.findmycarlocation.parking.data.local.ParkingLocalSource
import com.moggot.findmycarlocation.parking.data.local.ParkingSource
import com.moggot.findmycarlocation.parking.domain.ParkingInteractor
import com.moggot.findmycarlocation.parking.domain.ParkingInteractorImpl
import com.moggot.findmycarlocation.parking.domain.ParkingRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class ParkingModule {

    @Provides
    fun provideLocalSource(appDatabase: AppDatabase): ParkingSource =
        ParkingLocalSource(appDatabase.parkingDao())

    @Provides
    fun provideParkingRepo(parkingSource: ParkingSource): ParkingRepo =
        ParkingDataRepo(parkingSource)

    @Provides
    fun provideParkingInteractor(parkingRepo: ParkingRepo): ParkingInteractor =
        ParkingInteractorImpl(parkingRepo)
}
