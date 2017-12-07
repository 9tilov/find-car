package com.moggot.findmycarlocation.di.module;

import android.support.annotation.NonNull;

import com.moggot.findmycarlocation.data.api.LocationApi;
import com.moggot.findmycarlocation.data.repository.local.SettingsPreferences;
import com.moggot.findmycarlocation.data.repository.network.NetworkRepo;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(message -> Timber.d(message));
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return httpLoggingInterceptor;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Provides
    @Singleton
    Retrofit.Builder provideRetrofitBuilder(OkHttpClient client) {
        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    @Provides
    @Singleton
    LocationApi provideLocationApi(Retrofit.Builder builder) {
        return builder
                .baseUrl(LocationApi.BASE_LOCATION_URL)
                .build()
                .create(LocationApi.class);
    }

    @Provides
    @Singleton
    NetworkRepo provideNetworkService(@NonNull LocationApi locationApi, @NonNull SettingsPreferences preferences) {
        return new NetworkRepo(locationApi, preferences);
    }

    @Provides
    @Singleton
    Executor provideExecutor() {
        return Executors.newFixedThreadPool(2);
    }
}
