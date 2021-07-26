package com.moggot.findmycarlocation.data.model.parking

import com.google.android.gms.maps.model.LatLng

data class ParkingModel(val location: LatLng, val time: Long, val isParking: Boolean)
