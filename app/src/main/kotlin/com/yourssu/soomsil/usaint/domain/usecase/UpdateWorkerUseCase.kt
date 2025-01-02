package com.yourssu.soomsil.usaint.domain.usecase

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.yourssu.soomsil.usaint.di.TaskModule
import javax.inject.Inject

class UpdateWorkerUseCase @Inject constructor(
    private val workManager: WorkManager,
    @TaskModule.UpdateWorkRequest private val workRequest: PeriodicWorkRequest,
) {
    fun enqueue() {
        workManager.enqueueUniquePeriodicWork(
            "UpdateWorker",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
    }

    fun dequeue() {
        workManager.cancelUniqueWork("UpdateWorker")
    }
}