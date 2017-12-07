package com.moggot.findmycarlocation.data.repository.local;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.data.model.parking.ParkingModel;

import io.reactivex.Completable;
import io.reactivex.Single;

public class LocalRepo {

    @NonNull
    private final SettingsPreferences settingsPreferences;

    public LocalRepo(@NonNull SettingsPreferences settingsPreferences) {
        this.settingsPreferences = settingsPreferences;
    }

    public Completable saveParking(ParkingModel parkingModel) {
        if (settingsPreferences.loadParkingState()) {
            return Completable.error(() -> {
                throw new Exception("Can't save parking");
            });
        }
        settingsPreferences.saveTime(parkingModel.getTime());
        settingsPreferences.saveLocation(parkingModel.getLocation());
        settingsPreferences.saveParkingState(true);
        return Completable.complete();
    }

    public Single<ParkingModel> loadParking() {
        LatLng location = settingsPreferences.loadLocation();
        long time = settingsPreferences.loadTimeInMillis();
        boolean isParking = settingsPreferences.loadParkingState();
        ParkingModel parkingModel = new ParkingModel(location, time, isParking);
        return Single.fromCallable(() -> parkingModel);
    }

    public void changeParkingState(boolean state) {
        settingsPreferences.saveParkingState(state);
    }
}
