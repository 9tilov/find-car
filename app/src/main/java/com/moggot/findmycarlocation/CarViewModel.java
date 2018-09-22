package com.moggot.findmycarlocation;

import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.data.model.parking.ParkingModel;
import com.moggot.findmycarlocation.domain.LocationInteractor;
import com.moggot.findmycarlocation.domain.MainInteractor;

import java.util.Calendar;

import javax.inject.Inject;

public class CarViewModel extends BaseViewModel {

    private MainInteractor mainInteractor;
    private LocationInteractor locationInteractor;

    private MutableLiveData<Boolean> parkDataIfNeed = new MutableLiveData<>();

    @Inject
    public CarViewModel(MainInteractor mainInteractor, LocationInteractor locationInteractor) {
        this.mainInteractor = mainInteractor;
        this.locationInteractor = locationInteractor;
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
