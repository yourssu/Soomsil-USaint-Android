package com.yourssu.soomsil.usaint.di

import android.content.Context
import androidx.room.Room
import com.yourssu.soomsil.usaint.data.source.local.AppDatabase
import com.yourssu.soomsil.usaint.data.source.local.DatabaseMigrations
import com.yourssu.soomsil.usaint.data.source.local.dao.ChapelDao
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "my_database"
        )
            .addMigrations(DatabaseMigrations.MIGRATION_5_6)
            .build()
    }

    @Provides
    fun provideSemesterDao(db: AppDatabase): SemesterDao {
        return db.semesterDao()
    }

    @Provides
    fun provideLectureDao(db: AppDatabase): LectureDao {
        return db.lectureDao()
    }

    @Provides
    fun provideChapelDao(db: AppDatabase): ChapelDao {
        return db.chapelDao()
    }
}