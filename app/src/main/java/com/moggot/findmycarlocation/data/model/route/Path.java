package com.moggot.findmycarlocation.data.model.route;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Path {

    @SerializedName("routes")
    private final List<Route> routes = new ArrayList<>();

    public List<Route> getRoutes() {
        return routes;
    }
}
