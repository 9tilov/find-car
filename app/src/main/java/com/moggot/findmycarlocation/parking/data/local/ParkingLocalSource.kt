package com.moggot.findmycarlocation.parking.data.local

import com.google.android.gms.maps.model.LatLng
import com.moggot.findmycarlocation.parking.data.entity.ParkingData
import com.moggot.findmycarlocation.parking.data.local.mapper.toDataModel
import com.moggot.findmycarlocation.parking.data.local.mapper.toDbModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParkingLocalSource(private val parkingDao: ParkingDao) : ParkingSource {

    override suspend fun saveParkingPlace(parkingData: ParkingData) {
        parkingDao.insert(parkingData.toDbModel())
    }

    override fun getParkingPlace(): Flow<ParkingData?> {
        return parkingDao.get()
            .map { it?.toDataModel()?.copy(coords = LatLng(55.8575406, 37.4820292)) }
    }

    override suspend fun removeParkingPlace(id: Long) {
        parkingDao.remove(id)
    }
}
