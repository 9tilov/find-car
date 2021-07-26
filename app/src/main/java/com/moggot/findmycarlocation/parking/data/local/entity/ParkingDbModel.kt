package com.moggot.findmycarlocation.parking.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "parking")
data class ParkingDbModel(
    @PrimaryKey
    @ColumnInfo(name = "uid") val id: Long,
    @ColumnInfo(name = "coords") val coords: LatLng,
    @ColumnInfo(name = "time") val time: Long
)
