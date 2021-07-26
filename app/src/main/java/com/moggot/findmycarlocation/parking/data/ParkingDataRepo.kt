package com.moggot.findmycarlocation.parking.data

import android.util.Log
import com.moggot.findmycarlocation.parking.data.entity.ParkingData
import com.moggot.findmycarlocation.parking.data.local.ParkingSource
import com.moggot.findmycarlocation.parking.domain.ParkingRepo
import kotlinx.coroutines.flow.Flow

class ParkingDataRepo(private val parkingSource: ParkingSource) : ParkingRepo {

    override suspend fun saveParkingPlace(parkingData: ParkingData) {
        parkingSource.saveParkingPlace(parkingData)
    }

    override fun getParkingPlace(): Flow<ParkingData?> = parkingSource.getParkingPlace()

    override suspend fun removeParkingPlace(id: Long) {
        parkingSource.removeParkingPlace(id)
    }
}
