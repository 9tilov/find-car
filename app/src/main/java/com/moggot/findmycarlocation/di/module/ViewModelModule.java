package com.moggot.findmycarlocation.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.moggot.findmycarlocation.about.AboutViewModel;
import com.moggot.findmycarlocation.common.FactoryViewModel;
import com.moggot.findmycarlocation.di.scope.ViewModelKey;
import com.moggot.findmycarlocation.home.HomeViewModel;
import com.moggot.findmycarlocation.map.MapViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindCarViewModel(HomeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel.class)
    abstract ViewModel bindMapViewModel(MapViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AboutViewModel.class)
    abstract ViewModel bindAboutViewModel(AboutViewModel viewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(FactoryViewModel factory);
}
