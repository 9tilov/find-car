package com.moggot.findmycarlocation.parking.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.moggot.findmycarlocation.base.BaseViewModel
import com.moggot.findmycarlocation.constants.DataConstants.Companion.DEFAULT_COORDS
import com.moggot.findmycarlocation.parking.data.entity.ParkingData
import com.moggot.findmycarlocation.parking.domain.ParkingInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ParkingViewModel @Inject constructor(private val parkingInteractor: ParkingInteractor) :
    BaseViewModel() {

    val parkingData: LiveData<ParkingData?> =
        parkingInteractor.getParkingPlace().flowOn(Dispatchers.IO).asLiveData()

    fun parkCar(location: Location) {
        val tmpParkingData: ParkingData? = parkingData.value
        viewModelScope.launch(Dispatchers.IO) {
            if (tmpParkingData == null) {
                parkingInteractor.saveParkingPlace(
                    ParkingData(
                        coords = LatLng(location.latitude, location.longitude),
                        time = Calendar.getInstance().timeInMillis
                    )
                )
            } else {
                parkingInteractor.saveParkingPlace(
                    tmpParkingData.copy(
                        coords = LatLng(
                            location.latitude,
                            location.longitude
                        ), time = Calendar.getInstance().timeInMillis
                    )
                )
            }
        }
    }

    fun markCarIsFound() {
        viewModelScope.launch(Dispatchers.IO) {
            parkingData.value?.run {
                parkingInteractor.saveParkingPlace(
                    copy(
                        coords = DEFAULT_COORDS, time = Calendar.getInstance().timeInMillis
                    )
                )
            }

        }
    }

    fun isCarParked(): Boolean {
        if (parkingData.value == null) {
            return false
        }
        return parkingData.value?.coords?.latitude != 0.0 && parkingData.value?.coords?.longitude != 0.0
    }
}
