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

    // 1. Chapel 테이블의 모든 데이터를 가져오는 쿼리
    @Query("SELECT * FROM Chapel")
    fun getAllChapels(): Flow<List<ChapelEntity>>

    // 2. ChapelAttendance 테이블의 모든 데이터를 가져오는 쿼리
    @Query("SELECT * FROM ChapelAttendance")
    fun getAllAttendances(): Flow<List<ChapelAttendanceEntity>>

    @Upsert
    suspend fun upsertChapel(entity: ChapelEntity)

    @Upsert
    suspend fun upsertChapelAttendance(entity: ChapelAttendanceEntity)

    @Upsert
    suspend fun upsertChapelAttendances(entity: List<ChapelAttendanceEntity>)

    @Query("DELETE FROM Chapel WHERE year = :year AND semester = :semesterName")
    suspend fun deleteChapelEntitiesWithYearSemester(year: Int, semesterName: String)

    /**
     * @param division (division * 100000) + (year * 10) + semester.ordinal의 값으로 넣어주세요.
     */
    @Query("DELETE FROM Chapel WHERE division = :division")
    suspend fun deleteChapelEntityWithDivision(division: Long)

    /**
     * @param division (division * 100000) + (year * 10) + semester.ordinal의 값으로 넣어주세요.
     */
    @Query("DELETE FROM ChapelAttendance WHERE division = :division")
    suspend fun deleteChapelAttendancesEntitiesWithDivision(division: Long)

    @Query("DELETE FROM Chapel")
    suspend fun deleteAllChapel()

    @Query("DELETE FROM ChapelAttendance")
    suspend fun deleteAllAttendance()
}