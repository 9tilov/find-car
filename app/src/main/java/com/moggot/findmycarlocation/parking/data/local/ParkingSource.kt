package com.moggot.findmycarlocation.parking.data.local

import com.moggot.findmycarlocation.parking.data.entity.ParkingData
import kotlinx.coroutines.flow.Flow

interface ParkingSource {

    suspend fun saveParkingPlace(parkingData: ParkingData)
    fun getParkingPlace(): Flow<ParkingData?>
    suspend fun removeParkingPlace(id: Long)
}
