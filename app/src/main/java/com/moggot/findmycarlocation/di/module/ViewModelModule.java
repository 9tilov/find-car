package com.moggot.findmycarlocation.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.moggot.findmycarlocation.AboutFragment;
import com.moggot.findmycarlocation.AboutViewModel;
import com.moggot.findmycarlocation.CarViewModel;
import com.moggot.findmycarlocation.FactoryViewModel;
import com.moggot.findmycarlocation.MapViewModel;
import com.moggot.findmycarlocation.di.scope.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CarViewModel.class)
    abstract ViewModel bindCarViewModel(CarViewModel viewModel);

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
