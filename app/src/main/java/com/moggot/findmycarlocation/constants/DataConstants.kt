package com.moggot.findmycarlocation.constants

import com.google.android.gms.maps.model.LatLng

class DataConstants {
    companion object {
        const val DATABASE_NAME = "find_car_location"
        const val BASE_NAMESPACE = "com.moggot.findmycarlocation"
        const val DEFAULT_DATA_ID = 0L
        const val DEFAULT_TIME = 0L
        val DEFAULT_COORDS = LatLng(0.0, 0.0)
    }
}
