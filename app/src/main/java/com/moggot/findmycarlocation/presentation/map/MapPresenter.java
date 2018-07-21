package com.moggot.findmycarlocation.presentation.map;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.moggot.findmycarlocation.data.model.parking.ParkingModel;
import com.moggot.findmycarlocation.domain.LocationInteractor;
import com.moggot.findmycarlocation.domain.MainInteractor;
import com.moggot.findmycarlocation.domain.MapInteractor;
import com.moggot.findmycarlocation.presentation.common.BasePresenter;

import java.util.List;

import javax.inject.Inject;

public class MapPresenter extends BasePresenter<MapView> {

    @NonNull
    private final MapInteractor mapInteractor;
    @NonNull
    private final LocationInteractor locationInteractor;
    @NonNull
    private final MainInteractor mainInteractor;

    @Inject
    public MapPresenter(@NonNull MapInteractor mapInteractor,
                        @NonNull LocationInteractor locationInteractor,
                        @NonNull MainInteractor mainInteractor) {
        this.mapInteractor = mapInteractor;
        this.locationInteractor = locationInteractor;
        this.mainInteractor = mainInteractor;
    }

    public void buildRoute() {
        if (getView() == null) {
            return;
        }
        unSubscribeOnDetach(locationInteractor.getLocation()
                .flatMap(location -> {
                    LatLng originLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    return mapInteractor.getRoute(originLocation)
                            .filter(path -> !path.getRoutes().isEmpty())
                            .doOnSuccess(route -> {
                                getView().showAd();
                                String pointsStr = route.getRoutes().get(0).getOverviewPolyline().getPoints();
                                List<LatLng> points = PolyUtil.decode(pointsStr);
                                getView().showRoute(points);
                                String distance = route.getRoutes().get(0).getLegs().get(0).getDistance().getText();
                                String duration = route.getRoutes().get(0).getLegs().get(0).getDuration().getText();
                                getView().showDistance(distance);
                                getView().showDuration(duration);
                            });
                }).doOnError(throwable -> getView().showError())
                .subscribe());
    }

    public void drawCircle() {
        if (getView() != null) {
            ParkingModel parkingModel = mainInteractor.loadParkingData();
            getView().decoratePoint(parkingModel.getLocation());
        }
    }

    public void foundCar() {
        mainInteractor.markCarIsFound();
    }
}
