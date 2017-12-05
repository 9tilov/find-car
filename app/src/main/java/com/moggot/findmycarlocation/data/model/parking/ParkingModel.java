package com.moggot.findmycarlocation.data.model.parking;

import com.google.android.gms.maps.model.LatLng;

public class ParkingModel {

    private LatLng location;
    private long time;
    private boolean isParking;

    public ParkingModel(LatLng location, long time, boolean isParking) {
        this.location = location;
        this.time = time;
        this.isParking = isParking;
    }

    public LatLng getLocation() {
        return location;
    }

    public long getTime() {
        return time;
    }

    public boolean isParking() {
        return isParking;
    }
}
