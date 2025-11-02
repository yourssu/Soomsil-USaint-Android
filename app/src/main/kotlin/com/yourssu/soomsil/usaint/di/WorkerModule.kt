package com.yourssu.soomsil.usaint.di

import com.yourssu.soomsil.usaint.domain.interfaces.WorkerScheduler
import com.yourssu.soomsil.usaint.impl.AndroidWorkerScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {

    /**
     * WorkerScheduler 인터페이스에 AndroidWorkerScheduler 구현체를 바인딩합니다.
     * WorkManager와 PeriodicWorkRequest는 TaskModule에서 이미 제공됩니다.
     */
    @Binds
    @Singleton
    abstract fun bindWorkerScheduler(
        androidWorkerScheduler: AndroidWorkerScheduler
    ): WorkerScheduler
}

