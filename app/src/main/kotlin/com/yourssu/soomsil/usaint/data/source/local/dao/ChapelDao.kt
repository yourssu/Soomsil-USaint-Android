package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.yourssu.soomsil.usaint.data.source.local.entity.ChapelAttendanceEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.ChapelDataWithAttendance
import com.yourssu.soomsil.usaint.data.source.local.entity.ChapelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapelDao {
    @Transaction
    @Query("SELECT * FROM Chapel WHERE year = :year AND semester = :semesterName LIMIT 1")
    fun getChapel(year: Int, semesterName: String): Flow<ChapelDataWithAttendance>

    @Transaction
    @Query("SELECT * FROM Chapel LIMIT 1")
    fun getOneChapel(): Flow<List<ChapelDataWithAttendance>>


    @Transaction
    @Query("SELECT * FROM Chapel")
    fun getChapels(): Flow<List<ChapelDataWithAttendance>>

    @Upsert
    suspend fun upsertChapel(entity: ChapelEntity)

    @Upsert
    suspend fun upsertChapelAttendance(entity: ChapelAttendanceEntity)

    @Upsert
    suspend fun upsertChapelAttendances(entity: List<ChapelAttendanceEntity>)

    @Query("DELETE FROM Chapel WHERE year = :year AND semester = :semesterName")
    suspend fun deleteChapelEntitiesWithYearSemester(year: Int, semesterName: String)

    @Query("DELETE FROM ChapelAttendance WHERE division = :division")
    suspend fun deleteChapelAttendancesEntitiesWithDivision(division: Int)
}