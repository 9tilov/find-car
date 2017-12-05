package com.moggot.findmycarlocation.data.model.route;

import com.google.gson.annotations.SerializedName;

public class Leg {

    @SerializedName("distance")
    private Distance distance;
    @SerializedName("duration")
    private Duration duration;

    @SerializedName("steps")

    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }
}
