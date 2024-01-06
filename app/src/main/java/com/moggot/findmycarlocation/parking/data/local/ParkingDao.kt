package com.moggot.findmycarlocation.parking.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moggot.findmycarlocation.parking.data.local.entity.ParkingDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: ParkingDbModel)

    @Query("SELECT * FROM parking LIMIT 1")
    fun get(): Flow<ParkingDbModel?>

    @Query("DELETE FROM parking WHERE uid=:id")
    suspend fun remove(id: Long)
}
