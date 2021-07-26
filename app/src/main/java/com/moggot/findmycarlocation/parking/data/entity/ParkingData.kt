package com.moggot.findmycarlocation.parking.data.entity

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.moggot.findmycarlocation.constants.DataConstants.Companion.DEFAULT_DATA_ID
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParkingData(
    val id: Long = DEFAULT_DATA_ID,
    val coords: LatLng,
    val time: Long
) : Parcelable {

    fun wasCarParked(): Boolean {
        return coords.latitude != 0.0 && coords.longitude != 0.0
    }
}
