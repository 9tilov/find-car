package com.moggot.findmycarlocation.data.repository.local;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.data.model.parking.ParkingModel;

public class LocalRepo {

    @NonNull
    private final SettingsPreferences settingsPreferences;

    public LocalRepo(@NonNull SettingsPreferences settingsPreferences) {
        this.settingsPreferences = settingsPreferences;
    }

    public boolean saveParking(ParkingModel parkingModel) {
        if (settingsPreferences.loadParkingState()) {
            return false;
        }
        settingsPreferences.saveTime(parkingModel.getTime());
        settingsPreferences.saveLocation(parkingModel.getLocation());
        settingsPreferences.saveParkingState(true);
        return true;
    }

    public ParkingModel loadParking() {
        LatLng location = settingsPreferences.loadLocation();
        long time = settingsPreferences.loadTimeInMillis();
        boolean isParking = settingsPreferences.loadParkingState();
        return new ParkingModel(location, time, isParking);
    }

    public void changeParkingState(boolean state) {
        settingsPreferences.saveParkingState(state);
    }
}
