package com.moggot.findmycarlocation.home;

import androidx.annotation.NonNull;

import com.moggot.findmycarlocation.data.model.parking.ParkingModel;
import com.moggot.findmycarlocation.data.repository.local.LocalRepo;

import javax.inject.Inject;

public class MainInteractor {

    @NonNull
    private final LocalRepo repository;

    @Inject
    public MainInteractor(@NonNull LocalRepo localRepo) {
        this.repository = localRepo;
    }

    public void saveParkingData(ParkingModel parkingModel) {
        repository.parkCar(parkingModel);
    }

    public boolean carIsParked() {
        return repository.carIsParked();
    }

    public ParkingModel loadParkingData() {
        return repository.loadParkingData();
    }

    public void markCarIsFound() {
        repository.changeParkingState(false);
    }
}
