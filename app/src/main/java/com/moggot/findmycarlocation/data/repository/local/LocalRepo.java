package com.moggot.findmycarlocation.data.repository.local;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.data.model.parking.ParkingModel;

import javax.inject.Inject;

public class LocalRepo {

    @NonNull
    private final SettingsPreferences settingsPreferences;

    public LocalRepo(@NonNull SettingsPreferences settingsPreferences) {
        this.settingsPreferences = settingsPreferences;
    }

    public boolean carIsParked() {
        return settingsPreferences.isAlreadyParked();
    }

    public void parkCar(ParkingModel parkingModel) {
        settingsPreferences.saveTime(parkingModel.getTime());
        settingsPreferences.saveLocation(parkingModel.getLocation());
        settingsPreferences.saveParkingState(true);
    }

    public ParkingModel loadParkingData() {
        LatLng location = settingsPreferences.loadLocation();
        long time = settingsPreferences.loadTimeInMillis();
        boolean isParking = settingsPreferences.isAlreadyParked();
        return new ParkingModel(location, time, isParking);
    }

    public void changeParkingState(boolean state) {
        settingsPreferences.saveParkingState(state);
    }
}
