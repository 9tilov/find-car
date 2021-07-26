package com.moggot.findmycarlocation.di.component

import android.content.Context
import com.moggot.findmycarlocation.di.ApplicationProvider
import com.moggot.findmycarlocation.di.module.ContextModule
import com.moggot.findmycarlocation.di.module.DataModule
import com.moggot.findmycarlocation.di.module.LocationModule
import com.moggot.findmycarlocation.di.module.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            ContextModule::class]
)
interface AppComponent : ApplicationProvider {

    class Initializer private constructor() {
        companion object {
            fun init(context: Context): AppComponent {
                return DaggerAppComponent.builder()
                        .contextModule(ContextModule(context))
                        .build()
            }
        }
    }
}
