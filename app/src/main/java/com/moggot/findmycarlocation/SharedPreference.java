package com.moggot.findmycarlocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

public class SharedPreference {

    public static final String s_lat = "latitude";
    public static final String s_lng = "longitude";
    public static final String s_hour = "hour";
    public static final String s_minute = "minute";
    public static final String s_day = "day";
    public static final String s_state_location_save = "state_location";
    public static final String s_widget_id = "widget_id";

    static public void SaveLocation(Context ctx, double lat, double lng) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(s_lat);
        editor.remove(s_lng);
        editor.putLong(s_lat, Double.doubleToLongBits(lat));
        editor.putLong(s_lng, Double.doubleToLongBits(lng));
        editor.commit();
    }

    static public LatLng LoadLocation(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        double lat = Double.longBitsToDouble(sharedPreferences
                .getLong(s_lat, 0));
        double lng = Double.longBitsToDouble(sharedPreferences
                .getLong(s_lng, 0));
        LatLng latLng = new LatLng(lat, lng);
        return latLng;
    }

    static public void SaveTime(Context ctx, int day, int hour, int minute) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(s_day);
        editor.remove(s_hour);
        editor.remove(s_minute);
        editor.putInt(s_day, day);
        editor.putInt(s_hour, hour);
        editor.putInt(s_minute, minute);
        editor.commit();
    }

    static public String LoadTime(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int day = sharedPreferences.getInt(s_day, 0);
        int hour = sharedPreferences.getInt(s_hour, 0);
        int minute = sharedPreferences.getInt(s_minute, 0);
        String time = String.valueOf(day + " " + hour + ":" + minute);
        return time;
    }

    static public void SaveIsLocationSavedState(Context ctx, boolean state) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(s_state_location_save);
        editor.putBoolean(s_state_location_save, state);
        editor.commit();
    }

    static public boolean LoadIsLocationSavedState(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        boolean state = sharedPreferences.getBoolean(s_state_location_save,
                false);
        return state;
    }

    static public void clearPref(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(s_day);
        editor.remove(s_hour);
        editor.remove(s_minute);
        editor.remove(s_lat);
        editor.remove(s_lng);
    }

    static public void SaveWidgetID(Context ctx, int id) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(s_widget_id);
        editor.putInt(s_widget_id, id);
        editor.commit();
    }

    static public int LoadWidgetID(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int id = sharedPreferences.getInt(s_widget_id,
                -1);
        return id;
    }
}
