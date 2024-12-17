package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.yourssu.soomsil.usaint.data.source.local.entity.Semester

@Dao
interface SemesterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: Semester): List<Long>
}