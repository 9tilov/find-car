package com.moggot.findmycarlocation.data.repository.local;

import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import timber.log.Timber;

public class SettingsPreferences {

    private static final String PARKING_STATE = "parking_state";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String TIME = "time";
    private static final String SUGGEST_PREMIUM = "suggest_premium";
    private final SharedPreferences sharedPreferences;

    public SettingsPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void saveLocation(LatLng location) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(LATITUDE, (float) location.latitude);
        editor.putFloat(LONGITUDE, (float) location.longitude);
        editor.apply();
    }

    public LatLng loadLocation() {
        double latitude = sharedPreferences.getFloat(LATITUDE, .0f);
        double longitude = sharedPreferences.getFloat(LONGITUDE, .0f);
        Timber.d("loadLocation = " + latitude + "  " + longitude);
        return new LatLng(latitude, longitude);
    }

    public void saveTime(long timeInMillis) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(TIME, timeInMillis);
        editor.apply();
    }

    public long loadTimeInMillis() {
        long time = Calendar.getInstance().get(Calendar.MILLISECOND);
        return sharedPreferences.getLong(TIME, time);
    }

    public void saveParkingState(boolean isParking) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PARKING_STATE, isParking);
        if (!isParking) {
            editor.clear();
        }
        editor.apply();
    }

    public boolean isAlreadyParked() {
        return sharedPreferences.getBoolean(PARKING_STATE, false);
    }
}
