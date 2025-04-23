package com.yourssu.soomsil.usaint.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterEntity

@Database(
    entities = [SemesterEntity::class, LectureEntity::class],
    version = 4,
//    autoMigrations = [
//        AutoMigration(from = 4, to = 5),
//    ],
    exportSchema = false
)
@TypeConverters(MapTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun semesterDao(): SemesterDao
    abstract fun lectureDao(): LectureDao
}