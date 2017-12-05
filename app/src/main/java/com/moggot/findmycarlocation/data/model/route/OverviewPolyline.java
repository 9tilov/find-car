package com.moggot.findmycarlocation.data.model.route;

import com.google.gson.annotations.SerializedName;

public class OverviewPolyline {

    @SerializedName("points")
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

}
