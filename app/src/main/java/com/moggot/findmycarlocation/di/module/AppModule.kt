package com.moggot.findmycarlocation.di.module

import android.app.Application
import android.content.Context
import com.moggot.findmycarlocation.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideApplication(application: Application): App {
        return application as App
    }
}
