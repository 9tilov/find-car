package com.moggot.findmycarlocation.presentation.main;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.data.model.parking.ParkingModel;
import com.moggot.findmycarlocation.domain.LocationInteractor;
import com.moggot.findmycarlocation.domain.MainInteractor;
import com.moggot.findmycarlocation.presentation.common.BasePresenter;

import java.util.Calendar;

import javax.inject.Inject;

public class MainPresenter extends BasePresenter<MainView> {

    @NonNull
    private final MainInteractor mainInteractor;
    @NonNull
    private final LocationInteractor locationInteractor;

    @Inject
    public MainPresenter(@NonNull MainInteractor mainInteractor, @NonNull LocationInteractor locationInteractor) {
        this.mainInteractor = mainInteractor;
        this.locationInteractor = locationInteractor;
    }

    public void reParkCar() {
        mainInteractor.markCarIsFound();
        parkCar();
    }

    public void parkCar() throws SecurityException {
        if (getView() == null) {
            return;
        }
        unSubscribeOnDetach(locationInteractor.getLocation()
                .doOnSubscribe(disposable -> getView().enableGear(false))
                .doFinally(() -> getView().enableGear(true))
                .doOnError(throwable -> getView().showError())
                .subscribe(location -> {
                    LatLng coords = new LatLng(location.getLatitude(), location.getLongitude());
                    long time = Calendar.getInstance().get(Calendar.MILLISECOND);
                    ParkingModel parkingModel = new ParkingModel(coords, time, true);
                    if (mainInteractor.saveParkingData(parkingModel)) {
                        getView().showCarIsParking();
                    } else {
                        getView().showCantSaveParking();
                    }
                }));
    }

    public void initAd() {
        if (getView() != null) {
            getView().showAd();
        }
    }

    public void showMap() {
        if (getView() != null) {
            ParkingModel parkingModel = mainInteractor.loadParkingData();
            if (parkingModel.isParking()) {
                getView().openMap();
            } else {
                getView().showCarIsNotParking();
            }
        }
    }
}
