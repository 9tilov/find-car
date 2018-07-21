package com.moggot.findmycarlocation.domain;

import android.support.annotation.NonNull;

import com.moggot.findmycarlocation.data.model.parking.ParkingModel;
import com.moggot.findmycarlocation.data.repository.local.LocalRepo;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class MainInteractor {

    @NonNull
    private final LocalRepo repository;

    @Inject
    public MainInteractor(@NonNull LocalRepo localRepo) {
        this.repository = localRepo;
    }

    public boolean saveParkingData(ParkingModel parkingModel) {
        return repository.saveParking(parkingModel);
    }

    public ParkingModel loadParkingData() {
        return repository.loadParking();
    }

    public void markCarIsFound() {
        repository.changeParkingState(false);
    }
}
