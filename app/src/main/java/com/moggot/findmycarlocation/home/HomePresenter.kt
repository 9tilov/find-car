package com.moggot.findmycarlocation.home

import com.google.android.gms.maps.model.LatLng
import com.moggot.findmycarlocation.base.BasePresenter
import com.moggot.findmycarlocation.data.model.parking.ParkingModel
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class HomePresenter @Inject
constructor(private val mainInteractor: MainInteractor,
            private val locationInteractor: LocationInteractor) : BasePresenter<HomeView>() {

    fun forceParkCar() {
        mainInteractor.markCarIsFound()
        parkCarIfNeeded()
    }

    fun parkCarIfNeeded() {
        if (mainInteractor.carIsParked()) {
            view { showConfirmDialog() }
            return
        }
        Timber.d("parkCarIfNeeded")
        disposable.add(locationInteractor.location
                .subscribe({
                    Timber.d("subscribe")
                    val coords = LatLng(it.latitude, it.longitude)
                    val time = Calendar.getInstance().get(Calendar.MILLISECOND).toLong()
                    val parkingModel = ParkingModel(coords, time, true)
                    mainInteractor.saveParkingData(parkingModel)
                    view { animateParking() }
                },
                        { view { onError(it) } }))
    }

    fun tryToShowMap(): Boolean {
        return mainInteractor.loadParkingData().isParking
    }
}
