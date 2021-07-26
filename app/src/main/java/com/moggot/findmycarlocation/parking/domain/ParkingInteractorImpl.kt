package com.moggot.findmycarlocation.parking.domain

import com.moggot.findmycarlocation.parking.data.entity.ParkingData
import kotlinx.coroutines.flow.Flow

class ParkingInteractorImpl(private val parkingRepo: ParkingRepo) : ParkingInteractor {

    override suspend fun saveParkingPlace(parkingData: ParkingData) {
        parkingRepo.saveParkingPlace(parkingData)
    }

    override fun getParkingPlace(): Flow<ParkingData?> = parkingRepo.getParkingPlace()

    override suspend fun removeParkingPlace(id: Long) {
        parkingRepo.removeParkingPlace(id)
    }
}
