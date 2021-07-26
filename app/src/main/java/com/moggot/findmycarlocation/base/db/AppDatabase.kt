package com.moggot.findmycarlocation.base.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.moggot.findmycarlocation.base.db.AppDatabase.Companion.VERSION_NUMBER
import com.moggot.findmycarlocation.base.db.converters.LocationConverter
import com.moggot.findmycarlocation.parking.data.local.ParkingDao
import com.moggot.findmycarlocation.parking.data.local.entity.ParkingDbModel

@Database(
    entities = [
        ParkingDbModel::class
    ],
    version = VERSION_NUMBER,
    exportSchema = false
)
@TypeConverters(LocationConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun parkingDao(): ParkingDao

    companion object {
        const val VERSION_NUMBER = 1
    }
}
