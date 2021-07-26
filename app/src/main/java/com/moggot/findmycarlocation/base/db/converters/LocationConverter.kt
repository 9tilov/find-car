package com.moggot.findmycarlocation.base.db.converters

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.moggot.findmycarlocation.constants.DataConstants.Companion.DEFAULT_COORDS

object LocationConverter {

    @TypeConverter
    @JvmStatic
    fun fromLatLngToString(value: LatLng): String {
        return "${value.latitude}:${value.longitude}"
    }

    @TypeConverter
    @JvmStatic
    fun fromStringToLatLng(value: String): LatLng {
        val coordsArray = value.split(":")
        if (coordsArray.size != 2) {
            return DEFAULT_COORDS
        }
        return LatLng(coordsArray[0].toDouble(), coordsArray[1].toDouble())
    }
}
