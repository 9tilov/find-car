package com.moggot.findmycarlocation.map;

import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.billing.BillingManager;
import com.moggot.findmycarlocation.common.BaseViewModel;
import com.moggot.findmycarlocation.common.ErrorStatus;
import com.moggot.findmycarlocation.data.model.route.Path;
import com.moggot.findmycarlocation.home.LocationInteractor;
import com.moggot.findmycarlocation.home.MainInteractor;
import com.moggot.findmycarlocation.retry.RetryManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MapViewModel extends BaseViewModel {

    private final MutableLiveData<Path> routeData = new MutableLiveData<>();
    private final MutableLiveData<Location> locationData = new MutableLiveData<>();
    private final MutableLiveData<BillingManager> billing = new MutableLiveData<>();

    @NonNull
    private final MapInteractor mapInteractor;
    @NonNull
    private final LocationInteractor locationInteractor;
    @NonNull
    private final MainInteractor mainInteractor;
    @NonNull
    private final RetryManager retryManager;
    private final BillingManager mBillingManager;

    @Inject
    public MapViewModel(@NonNull MapInteractor mapInteractor,
                        @NonNull LocationInteractor locationInteractor,
                        @NonNull MainInteractor mainInteractor,
                        @NonNull RetryManager retryManager,
                        BillingManager billingManager) {
        this.mapInteractor = mapInteractor;
        this.locationInteractor = locationInteractor;
        this.mainInteractor = mainInteractor;
        this.retryManager = retryManager;
        mBillingManager = billingManager;
        billing.setValue(billingManager);
        addObserver(routeData);
        addObserver(locationData);
    }

    public boolean canShowAds() {
        return mBillingManager.isPremium();
    }

    public void buildRoute() {
        if (!mainInteractor.loadParkingData().isParking()) {
            return;
        }
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

    public void getCurrentLocation() {
        compositeDisposable.add(locationInteractor.getLocation()
                .doOnError(throwable -> errorStatus.setValue(new ErrorStatus(ErrorStatus.LOCATION_ERROR)))
                .subscribe(locationData::postValue));
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

    public MutableLiveData<Location> getLocationData() {
        return locationData;
    }

    public MutableLiveData<BillingManager> getBilling() {
        return billing;
    }

    @Override
    public void onCleared() {
        super.onCleared();
        mBillingManager.destroy();
    }
}
