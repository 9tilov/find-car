package com.moggot.findmycarlocation.map.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.moggot.findmycarlocation.base.BaseViewModel
import com.moggot.findmycarlocation.base.data.Result
import com.moggot.findmycarlocation.base.data.Status
import com.moggot.findmycarlocation.data.model.route.Path
import com.moggot.findmycarlocation.map.domain.MapInteractor
import com.moggot.findmycarlocation.parking.domain.ParkingInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapInteractor: MapInteractor,
    private val parkingInteractor: ParkingInteractor
) : BaseViewModel() {

    private val routeData = MutableLiveData<Result<Path>>()
    val parkingData = parkingInteractor.getParkingPlace().flowOn(Dispatchers.IO).asLiveData()
    lateinit var currentLocation: LatLng

    fun buildRoute(latitude: Double, longitude: Double) = viewModelScope.launch(Dispatchers.Main) {
        currentLocation = LatLng(latitude, longitude)
        mapInteractor.getRoute(latitude, longitude)
            .flowOn(Dispatchers.IO)
            .catch { Result(Status.ERROR, it) }
            .collect { routeData.value = Result(Status.SUCCESS, it) }
    }

    fun retryCall() {
        buildRoute(currentLocation.latitude, currentLocation.longitude)
    }

    fun foundCar() = viewModelScope.launch(Dispatchers.IO) {
        val parkingData = parkingData.value
        if (parkingData != null) {
            parkingInteractor.removeParkingPlace(parkingData.id)
        }
    }

    fun getRouteData(): LiveData<Result<Path>> = routeData
}
