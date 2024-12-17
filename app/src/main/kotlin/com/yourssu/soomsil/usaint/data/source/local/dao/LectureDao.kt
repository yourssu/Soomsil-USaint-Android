package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.yourssu.soomsil.usaint.data.source.local.entity.Lecture

@Dao
interface LectureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLectures(vararg lectures: Lecture): List<Long>
}