package com.moggot.findmycarlocation.parking.data.local.mapper

import com.moggot.findmycarlocation.parking.data.entity.ParkingData
import com.moggot.findmycarlocation.parking.data.local.entity.ParkingDbModel

fun ParkingDbModel.toDataModel(): ParkingData = ParkingData(id, coords, time)
fun ParkingData.toDbModel(): ParkingDbModel = ParkingDbModel(id, coords, time)
