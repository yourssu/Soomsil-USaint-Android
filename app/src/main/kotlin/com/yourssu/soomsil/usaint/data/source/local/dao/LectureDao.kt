package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yourssu.soomsil.usaint.data.source.local.entity.Lecture

@Dao
interface LectureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLecture(lecture: Lecture): Long

    @Query("SELECT * FROM Lecture WHERE code = :code LIMIT 1")
    fun getLectureByCode(code: String): Lecture?

    @Query("SELECT * FROM Lecture WHERE semesterId = :semesterId")
    fun getLecturesBySemesterId(semesterId: Int): List<Lecture>
}