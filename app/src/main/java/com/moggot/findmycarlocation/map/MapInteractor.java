package com.moggot.findmycarlocation.map;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.moggot.findmycarlocation.data.model.route.Path;
import com.moggot.findmycarlocation.data.repository.network.NetworkRepo;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapInteractor {

    @NonNull
    private final NetworkRepo networkRepo;

    @Inject
    public MapInteractor(@NonNull NetworkRepo networkRepo) {
        this.networkRepo = networkRepo;
    }

    public Single<Path> getRoute(LatLng currentLocation) {
        return networkRepo.getRoute(currentLocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
}
