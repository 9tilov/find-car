package com.moggot.findmycarlocation.parking.domain

import com.moggot.findmycarlocation.parking.data.entity.ParkingData
import kotlinx.coroutines.flow.Flow

interface ParkingRepo {

    suspend fun saveParkingPlace(parkingData: ParkingData)
    fun getParkingPlace(): Flow<ParkingData?>
    suspend fun removeParkingPlace(id: Long)
}
