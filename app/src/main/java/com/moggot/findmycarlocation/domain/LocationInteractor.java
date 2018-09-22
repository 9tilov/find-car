package com.moggot.findmycarlocation.domain;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;
import com.moggot.findmycarlocation.data.repository.local.LocalRepo;
import com.patloew.rxlocation.RxLocation;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LocationInteractor {

    @NonNull
    private final RxLocation rxLocation;
    @NonNull
    private final LocationRequest locationRequest;

    @Inject
    public LocationInteractor(@NonNull RxLocation rxLocation,
                              @NonNull LocationRequest locationRequest) {
        this.rxLocation = rxLocation;
        this.locationRequest = locationRequest;
    }

    public Maybe<Location> getLocation() throws SecurityException {
        return rxLocation.settings().checkAndHandleResolution(locationRequest)
                .flatMapObservable(this::getLocationObservable)
                .firstElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Location> getLocationObservable(boolean success) throws SecurityException {
        if (success) {
            return rxLocation.location().updates(locationRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

        } else {
            return rxLocation.location().lastLocation()
                    .toObservable();
        }
    }
}
