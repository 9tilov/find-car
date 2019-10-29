package com.moggot.findmycarlocation.di.component

import com.moggot.findmycarlocation.di.ApplicationProvider
import com.moggot.findmycarlocation.di.ComponentHolder
import com.moggot.findmycarlocation.di.module.DataModule
import com.moggot.findmycarlocation.di.module.LocationModule
import com.moggot.findmycarlocation.di.module.NetworkModule
import com.moggot.findmycarlocation.di.scope.MainScope
import com.moggot.findmycarlocation.home.HomeFragment
import com.moggot.findmycarlocation.map.GoogleMapFragment
import dagger.Component

@MainScope
@Component(dependencies = [ApplicationProvider::class], modules = [
    DataModule::class,
    LocationModule::class,
    NetworkModule::class])
interface MainComponent {

    fun inject(homeFragment: HomeFragment)
    fun inject(googleMapFragment: GoogleMapFragment)

    class Initializer private constructor() {
        companion object {
            fun init(): MainComponent {
                return DaggerMainComponent.builder()
                        .applicationProvider(ComponentHolder.provideApplicationProvider())
                        .dataModule(DataModule())
                        .locationModule(LocationModule())
                        .networkModule(NetworkModule())
                        .build()
            }
        }
    }

}