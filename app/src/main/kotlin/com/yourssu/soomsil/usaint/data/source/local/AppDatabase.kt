package com.yourssu.soomsil.usaint.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.dao.TotalReportCardDao
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCardVO

@Database(
    entities = [TotalReportCardVO::class, SemesterVO::class, LectureVO::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun totalReportCardDao(): TotalReportCardDao
    abstract fun semesterDao(): SemesterDao
    abstract fun lectureDao(): LectureDao
}