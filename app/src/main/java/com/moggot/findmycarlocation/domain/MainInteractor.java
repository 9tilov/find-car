package com.moggot.findmycarlocation.domain;

import android.support.annotation.NonNull;

import com.moggot.findmycarlocation.data.model.parking.ParkingModel;
import com.moggot.findmycarlocation.data.repository.local.LocalRepo;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainInteractor {

    @NonNull
    private LocalRepo repository;

    @Inject
    public MainInteractor(@NonNull LocalRepo localRepo) {
        this.repository = localRepo;
    }

    public Completable saveParkingData(ParkingModel parkingModel) {
        return repository.saveParking(parkingModel);
    }

    public Single<ParkingModel> loadParkingData() {
        return repository.loadParking()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void markCarIsFound() {
        repository.changeParkingState(false);
    }
}
