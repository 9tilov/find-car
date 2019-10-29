package com.moggot.findmycarlocation.map

import com.google.android.gms.maps.model.LatLng
import com.moggot.findmycarlocation.base.BasePresenter
import com.moggot.findmycarlocation.home.LocationInteractor
import com.moggot.findmycarlocation.home.MainInteractor
import com.moggot.findmycarlocation.retry.RetryManager
import timber.log.Timber

import javax.inject.Inject

class MapPresenter @Inject
constructor(private val mapInteractor: MapInteractor,
            private val locationInteractor: LocationInteractor,
            private val mainInteractor: MainInteractor,
            private val retryManager: RetryManager) : BasePresenter<GoogleMapView>() {

    fun buildRoute() {
        if (!mainInteractor.loadParkingData().isParking) {
            return
        }
        Timber.d("buildRoute")
        disposable.add(locationInteractor.location
                .flatMap { location ->
                    val originLocation = LatLng(location.latitude, location.longitude)
                    mapInteractor.getRoute(originLocation)
                            .filter { path -> path.routes.isNotEmpty() }
                            .doOnSuccess {
                                Timber.d("doOnSuccess")
                                view { drawRoute(it) } }
                }.doOnError {
                    Timber.d("onerror")
                    view { onError(it) }
                }
                .toObservable()
                .retryWhen { retryHandler -> retryHandler.flatMap(retryManager::observeRetries) }
                .subscribe())
    }

    fun getCurrentLocation() {
        disposable.add(locationInteractor.location
                .doOnError { throwable -> view { onError(throwable) } }
                .subscribe { view { updateLocation(it) } })
    }

    fun drawCircle(): LatLng {
        return mainInteractor.loadParkingData().location
    }

    fun retryCall() {
        retryManager.retry()
    }

    fun foundCar() {
        mainInteractor.markCarIsFound()
        view { onCarFound() }
    }
}
