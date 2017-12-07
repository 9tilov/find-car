package com.moggot.findmycarlocation.data.model.route;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Route {

    @SerializedName("legs")
    private final List<Leg> legs = new ArrayList<>();

    @SerializedName("overview_polyline")
    private OverviewPolyline overviewPolyline;

    public OverviewPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public List<Leg> getLegs() {
        return legs;
    }
}
