package com.yourssu.soomsil.usaint

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yourssu.soomsil.usaint.data.repository.CurrentSemesterRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.domain.usecase.LecturesDiffUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class UpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val lecturesDiffUseCase: LecturesDiffUseCase,
    private val uSaintSessionRepo: USaintSessionRepository,
    private val currentSemesterRepo: CurrentSemesterRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        // 작업 수행
        val session = uSaintSessionRepo.getSession().getOrElse { e ->
            Timber.e(e)
            return Result.failure()
        }
        val oldLectures = currentSemesterRepo.getLocalCurrentSemesterLectures().getOrElse { e ->
            Timber.e(e)
            return Result.failure()
        }
        val newLectures =
            currentSemesterRepo.getRemoteCurrentSemesterLectures(session).getOrElse { e ->
                Timber.e(e)
                return Result.failure()
            }


        // 알림 띄우기
        showNotification(
            "성적 업데이트",
            "[기업가정신과행동] 성적이 업데이트 되었습니다. ${newLectures.toString()}"
        ) // 알림 발행
        Timber.d("WorkManager: show notification time : ${getCurrentTimeInHoursAndMinutes()}")

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val channelId = "work_manager_channel"
        val channelName = "WorkManagerPushChannel"

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)


        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // 알림 발행
        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notificationBuilder.build()
        )
    }
}

fun getCurrentTimeInHoursAndMinutes(): String {
    val currentTimeMillis = System.currentTimeMillis()
    val date = Date(currentTimeMillis)
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(date)
}
