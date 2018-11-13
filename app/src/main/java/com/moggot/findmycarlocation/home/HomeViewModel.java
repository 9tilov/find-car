package com.moggot.findmycarlocation.home;

import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.billing.AdsEventListener;
import com.moggot.findmycarlocation.billing.BillingManager;
import com.moggot.findmycarlocation.common.BaseViewModel;
import com.moggot.findmycarlocation.common.ErrorStatus;
import com.moggot.findmycarlocation.data.model.parking.ParkingModel;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HomeViewModel extends BaseViewModel {

    private final MainInteractor mainInteractor;
    private final LocationInteractor locationInteractor;
    private final BillingManager mBillingManager;

    private final MutableLiveData<Boolean> parkDataIfNeed = new MutableLiveData<>();

    @Inject
    public HomeViewModel(MainInteractor mainInteractor,
                         LocationInteractor locationInteractor,
                         BillingManager billingManager) {
        this.mainInteractor = mainInteractor;
        this.locationInteractor = locationInteractor;
        mBillingManager = billingManager;
        addObserver(parkDataIfNeed);
    }

    public void checkBilling(AdsEventListener adsEventListener) {
        mBillingManager.setAdsShowListener(adsEventListener);
        mBillingManager.startConnection();
    }

    public boolean canShowAds() {
        return mBillingManager.isPremium();
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

    @Override
    public void onCleared() {
        super.onCleared();
        mBillingManager.destroy();
    }
}
