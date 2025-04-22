package com.yourssu.soomsil.usaint.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO

@Database(
    entities = [SemesterVO::class, LectureVO::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun semesterDao(): SemesterDao
    abstract fun lectureDao(): LectureDao
}