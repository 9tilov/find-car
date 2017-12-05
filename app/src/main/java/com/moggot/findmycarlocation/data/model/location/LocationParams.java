package com.moggot.findmycarlocation.data.model.location;

import com.google.android.gms.maps.model.LatLng;

public class LocationParams {

    private LatLng origin;
    private LatLng destination;

    public LocationParams(LatLng origin, LatLng destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public LatLng getDestination() {
        return destination;
    }
}
