package com.moggot.findmycarlocation.map.data

import android.util.Log
import com.moggot.findmycarlocation.data.api.LocationApi
import com.moggot.findmycarlocation.data.model.route.Path
import com.moggot.findmycarlocation.map.domain.MapRepo
import com.moggot.findmycarlocation.parking.data.local.ParkingDao
import com.moggot.findmycarlocation.parking.data.local.mapper.toDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MapDataRepo(
    private val locationApi: LocationApi,
    private val parkingDao: ParkingDao
) : MapRepo {

    override fun buildRoute(latitude: Double, longitude: Double): Flow<Path> {
        return parkingDao.get().map {
            if (it == null) {
                throw IllegalStateException("Current location is undefined")
            }
            val originStr = "$latitude,$longitude"
            val locationDataModel = it.toDataModel()
            val destinationStr: String =
                locationDataModel.coords.latitude.toString() + "," + locationDataModel.coords.longitude.toString()
            locationApi.getLocation(originStr, destinationStr)
        }
    }
}
