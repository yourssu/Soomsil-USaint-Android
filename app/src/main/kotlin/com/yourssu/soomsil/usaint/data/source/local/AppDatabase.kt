package com.yourssu.soomsil.usaint.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.dao.TotalReportCardDao
import com.yourssu.soomsil.usaint.data.source.local.entity.Lecture
import com.yourssu.soomsil.usaint.data.source.local.entity.Semester
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCard

@Database(
    entities = [TotalReportCard::class, Semester::class, Lecture::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun totalReportCardDao(): TotalReportCardDao
    abstract fun semesterDao(): SemesterDao
    abstract fun lectureDao(): LectureDao
}