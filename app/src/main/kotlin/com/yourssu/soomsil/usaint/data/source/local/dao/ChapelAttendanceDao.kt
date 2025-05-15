package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.yourssu.soomsil.usaint.data.source.local.entity.ChapelAttendanceEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.ChapelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapelAttendanceDao {
//    @Query("SELECT * FROM ChapelAttendance WHERE division = :division")
    @Query(
        """
        SELECT * FROM ChapelAttendance
        """
    )
    fun getChapelAttendancesDivision(): Flow<List<ChapelAttendanceEntity>>

    @Query(
        """
        SELECT * FROM Chapel
        JOIN ChapelAttendance ON Chapel.division = ChapelAttendance.division
        """
    )
    fun getChapelAttendances(): Flow<Map<ChapelEntity, List<ChapelAttendanceEntity>>>

    @Upsert
    suspend fun upsertChapelAttendance(entity: ChapelAttendanceEntity)

    @Upsert
    suspend fun upsertChapelAttendances(entity: List<ChapelAttendanceEntity>)

    @Query("DELETE FROM ChapelAttendance WHERE division = :division")
    suspend fun deleteChapelAttendancesEntitiesWithDivision(division: Int)
}