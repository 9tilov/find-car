package com.moggot.findmycarlocation.home;

import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.common.BaseViewModel;
import com.moggot.findmycarlocation.common.ErrorStatus;
import com.moggot.findmycarlocation.data.model.parking.ParkingModel;

import java.util.Calendar;

import javax.inject.Inject;

public class HomeViewModel extends BaseViewModel {

    private final MainInteractor mainInteractor;
    private final LocationInteractor locationInteractor;

    private final MutableLiveData<Boolean> parkDataIfNeed = new MutableLiveData<>();

    @Inject
    public HomeViewModel(MainInteractor mainInteractor,
                         LocationInteractor locationInteractor) {
        this.mainInteractor = mainInteractor;
        this.locationInteractor = locationInteractor;
        addObserver(parkDataIfNeed);
    }

    public void reParkCar() {
        mainInteractor.markCarIsFound();
        parkCar();
    }

    public void parkCar() throws SecurityException {
        compositeDisposable.add(locationInteractor.getLocation()
                .doOnError(throwable -> errorStatus.postValue(new ErrorStatus(ErrorStatus.LOCATION_ERROR, throwable)))
                .subscribe(location -> {
                    LatLng coords = new LatLng(location.getLatitude(), location.getLongitude());
                    long time = Calendar.getInstance().get(Calendar.MILLISECOND);
                    ParkingModel parkingModel = new ParkingModel(coords, time, true);
                    parkDataIfNeed.setValue(mainInteractor.saveParkingData(parkingModel));
                }));
    }

    public boolean tryToShowMap() {
        return mainInteractor.loadParkingData().isParking();
    }

    public MutableLiveData<Boolean> parkDataIfNeed() {
        return parkDataIfNeed;
    }
}
