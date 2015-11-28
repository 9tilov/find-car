package com.moggot.findmycarlocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

public class SharedPreference {

    public class ACTIVITY_RESULT_CODE {
        final static int MAP_SCREEN = 0;
        final static int INFO_SCREEN = 1;
    }

    public static final String s_lat = "latitude";
    public static final String s_lng = "longitude";
    public static final String s_hour = "hour";
    public static final String s_minute = "minute";
    public static final String s_day = "day";
    public static final String s_state_location_save = "state_location";
    public static final String s_widget_installed = "widget_installed";
    public static final String s_rating_count = "rating_count";
    public static final String s_widget_id = "widget_id";

    static public void SaveLocation(Context ctx, double lat, double lng) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(s_lat);
        editor.remove(s_lng);
        editor.putLong(s_lat, Double.doubleToLongBits(lat));
        editor.putLong(s_lng, Double.doubleToLongBits(lng));
        editor.apply();
    }

    static public LatLng LoadLocation(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        double lat = Double.longBitsToDouble(sharedPreferences
                .getLong(s_lat, 0));
        double lng = Double.longBitsToDouble(sharedPreferences
                .getLong(s_lng, 0));
        return new LatLng(lat, lng);
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
        editor.apply();
    }

    static public String LoadTime(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int day = sharedPreferences.getInt(s_day, 0);
        int hour = sharedPreferences.getInt(s_hour, 0);
        int minute = sharedPreferences.getInt(s_minute, 0);
        return String.valueOf(day + " " + hour + ":" + minute);
    }

    static public void SaveIsLocationSavedState(Context ctx, boolean state) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(s_state_location_save);
        editor.putBoolean(s_state_location_save, state);
        editor.apply();
    }

    static public boolean LoadIsLocationSavedState(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        return sharedPreferences.getBoolean(s_state_location_save,
                false);
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

    static public void SaveInstallWidgetState(Context ctx, boolean state) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(s_widget_installed);
        editor.putBoolean(s_widget_installed, state);
        editor.apply();
    }

    static public boolean LoadInstallWidgetState(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        return sharedPreferences.getBoolean(s_widget_installed, false);
    }

    static public void SaveRatingCount(Context ctx, int rate_count) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(s_rating_count);
        editor.putInt(s_rating_count, rate_count);
        editor.apply();
    }

    static public int LoadRatingCount(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        return sharedPreferences.getInt(s_rating_count, 0);
    }

    static public void SaveWidgetID(Context ctx, int widgetID) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(s_widget_id);
        editor.putInt(s_widget_id, widgetID);
        editor.apply();
    }

    static public int LoadWidgetID(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        return sharedPreferences.getInt(s_widget_id, 0);
    }

}
