package com.moggot.findmycarlocation;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.data.model.route.Path;
import com.moggot.findmycarlocation.domain.LocationInteractor;
import com.moggot.findmycarlocation.domain.MainInteractor;
import com.moggot.findmycarlocation.domain.MapInteractor;
import com.moggot.findmycarlocation.retry.RetryManager;

import javax.inject.Inject;

public class MapViewModel extends BaseViewModel {

    private final MutableLiveData<Path> routeData = new MutableLiveData<>();

    @NonNull
    private final MapInteractor mapInteractor;
    @NonNull
    private final LocationInteractor locationInteractor;
    @NonNull
    private final MainInteractor mainInteractor;
    @NonNull
    private final RetryManager retryManager;

    @Inject
    public MapViewModel(@NonNull MapInteractor mapInteractor,
                        @NonNull LocationInteractor locationInteractor,
                        @NonNull MainInteractor mainInteractor,
                        @NonNull RetryManager retryManager) {
        this.mapInteractor = mapInteractor;
        this.locationInteractor = locationInteractor;
        this.mainInteractor = mainInteractor;
        this.retryManager = retryManager;
    }

    public void buildRoute() {
        compositeDisposable.add(locationInteractor.getLocation()
                .flatMap(location -> {
                    LatLng originLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    return mapInteractor.getRoute(originLocation)
                            .filter(path -> !path.getRoutes().isEmpty())
                            .doOnSuccess(routeData::postValue);
                }).doOnError(throwable -> errorStatus.setValue(new ErrorStatus(ErrorStatus.LOCATION_ERROR)))
                .toObservable()
                .retryWhen(retryHandler -> retryHandler.flatMap(retryManager::observeRetries))
                .subscribe());
    }

    public LatLng drawCircle() {
        return mainInteractor.loadParkingData().getLocation();
    }

    public void retryCall() {
        retryManager.retry();
    }

    public void foundCar() {
        mainInteractor.markCarIsFound();
    }

    public MutableLiveData<Path> getRouteData() {
        return routeData;
    }
}
