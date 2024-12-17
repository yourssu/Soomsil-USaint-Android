package com.yourssu.soomsil.usaint.di

import android.content.Context
import androidx.room.Room
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.source.local.AppDatabase
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.dao.TotalReportCardDao
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
        ).build()
    }

    @Provides
    fun provideTotalReportCardDao(db: AppDatabase): TotalReportCardDao {
        return db.totalReportCardDao()
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
    @Singleton
    fun provideReportCardRepository(
        totalReportCardDao: TotalReportCardDao,
        semesterDao: SemesterDao,
        lectureDao: LectureDao
    ): ReportCardRepository {
        return ReportCardRepository(totalReportCardDao, semesterDao, lectureDao)
    }
}