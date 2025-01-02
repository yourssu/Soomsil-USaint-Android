package com.yourssu.soomsil.usaint.di

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yourssu.soomsil.usaint.UpdateWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class UpdateWorkRequest

    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Singleton
    @Provides
    @UpdateWorkRequest
    fun provideUpdateWorkRequest(): PeriodicWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        return PeriodicWorkRequestBuilder<UpdateWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
    }
}