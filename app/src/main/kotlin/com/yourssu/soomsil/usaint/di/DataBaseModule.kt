package com.yourssu.soomsil.usaint.di

import android.content.Context
import androidx.room.Room
import com.yourssu.soomsil.usaint.BuildConfig
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
        ).apply {
            // fallbackToDestructiveMigration() : 데이터베이스 버전이 변경되었을 때 기존 데이터를 보존하지 않고 데이터베이스를 재구성하는 방법
            // 디버그 모드에서만 사용해야 함
            if (BuildConfig.DEBUG) fallbackToDestructiveMigration()
        }.build()
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
}