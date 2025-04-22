package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity

@Dao
interface LectureDao {
    @Upsert
    suspend fun upsertLectures(lectures: List<LectureEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: LectureEntity): Long

    @Query("SELECT * FROM Lecture WHERE code = :code LIMIT 1")
    suspend fun getLectureByCode(code: String): LectureEntity?

    @Query("SELECT * FROM Lecture WHERE year = :year AND semester = :semester")
    suspend fun getLecturesByYearAndSemester(year: Int, semester: String): List<LectureEntity>

    @Query("DELETE FROM Lecture")
    suspend fun deleteLectures()
}