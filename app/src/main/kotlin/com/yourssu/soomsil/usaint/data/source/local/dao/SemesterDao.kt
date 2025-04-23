package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterWithLectures
import kotlinx.coroutines.flow.Flow

@Dao
interface SemesterDao {
    @Query("SELECT * FROM Semester WHERE year = :year AND semester = :semesterName")
    fun getSemesterEntity(year: Int, semesterName: String): Flow<SemesterEntity>

    @Query("SELECT * FROM Semester")
    fun getSemesterEntities(): Flow<List<SemesterEntity>>

    @Query("SELECT * FROM Semester")
    suspend fun getOneOffSemesterEntities(): List<SemesterEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreSemesters(entities: List<SemesterEntity>): List<Long>

    @Upsert
    suspend fun upsertSemesters(entities: List<SemesterEntity>)

    @Transaction
    @Query("SELECT * FROM Semester")
    fun getSemesterWithLectures(): Flow<List<SemesterWithLectures>>

    @Query("DELETE FROM Semester")
    suspend fun deleteAllSemesters()
}