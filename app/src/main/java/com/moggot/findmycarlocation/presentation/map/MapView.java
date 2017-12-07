package com.moggot.findmycarlocation.presentation.map;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.presentation.common.BaseView;

import java.util.List;

public interface MapView extends BaseView {

    void showRoute(List<LatLng> points);

    void showDistance(String distance);

    void showDuration(String duration);

    void decoratePoint(LatLng location);

    void showAd();
}
