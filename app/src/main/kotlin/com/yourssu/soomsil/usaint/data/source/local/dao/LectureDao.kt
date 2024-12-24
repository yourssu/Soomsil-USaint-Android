package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO

@Dao
interface LectureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: LectureVO): Long

    @Query("SELECT * FROM Lecture WHERE code = :code LIMIT 1")
    suspend fun getLectureByCode(code: String): LectureVO?

    @Query("SELECT * FROM Lecture WHERE semesterId = :semesterId")
    suspend fun getLecturesBySemesterId(semesterId: Int): List<LectureVO>

    @Query("DELETE FROM Lecture")
    suspend fun deleteAll()
}