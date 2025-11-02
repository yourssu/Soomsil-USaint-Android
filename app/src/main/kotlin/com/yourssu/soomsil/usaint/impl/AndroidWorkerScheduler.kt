package com.yourssu.soomsil.usaint.impl

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.yourssu.soomsil.usaint.di.TaskModule
import com.yourssu.soomsil.usaint.domain.interfaces.WorkerScheduler
import javax.inject.Inject

/**
 * WorkerScheduler 인터페이스의 Android 구현체
 * WorkManager를 사용하여 Worker를 스케줄링합니다.
 */
class AndroidWorkerScheduler @Inject constructor(
    private val workManager: WorkManager,
    @TaskModule.UpdateWorkRequest private val workRequest: PeriodicWorkRequest,
) : WorkerScheduler {

    override fun enqueue() {
        workManager.enqueueUniquePeriodicWork(
            "UpdateWorker",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
    }

    override fun dequeue() {
        workManager.cancelUniqueWork("UpdateWorker")
    }
}

