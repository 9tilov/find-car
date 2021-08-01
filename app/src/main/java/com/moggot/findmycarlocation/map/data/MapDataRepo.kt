package com.moggot.findmycarlocation.map.data

import android.util.Log
import com.moggot.findmycarlocation.data.api.LocationApi
import com.moggot.findmycarlocation.data.model.route.Path
import com.moggot.findmycarlocation.map.domain.MapRepo
import com.moggot.findmycarlocation.parking.data.local.ParkingDao
import com.moggot.findmycarlocation.parking.data.local.ParkingSource
import com.moggot.findmycarlocation.parking.data.local.mapper.toDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MapDataRepo(
    private val locationApi: LocationApi,
    private val parkingSource: ParkingSource
) : MapRepo {

    override fun buildRoute(latitude: Double, longitude: Double): Flow<Path> {
        return parkingSource.getParkingPlace().map {
            if (it == null) {
                throw IllegalStateException("Current location is undefined")
            }
            val originStr = "$latitude,$longitude"
            // "55.8575406,37.4820292"
            val destinationStr: String =
                it.coords.latitude.toString() + "," + it.coords.longitude.toString()
            Log.d("moggot123", "buildRoute: " + destinationStr)
            locationApi.getLocation(originStr, destinationStr)
        }
    }
}
