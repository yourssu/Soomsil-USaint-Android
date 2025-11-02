package com.yourssu.soomsil.usaint.domain.usecase

import com.yourssu.soomsil.usaint.domain.interfaces.WorkerScheduler
import javax.inject.Inject

class UpdateWorkerUseCase @Inject constructor(
    private val workerScheduler: WorkerScheduler
) {
    fun enqueue() {
        workerScheduler.enqueue()
    }

    fun dequeue() {
        workerScheduler.dequeue()
    }
}