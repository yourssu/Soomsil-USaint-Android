package com.yourssu.soomsil.usaint

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yourssu.soomsil.usaint.data.repository.LectureRepository
import com.yourssu.soomsil.usaint.data.repository.SemesterRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterTypeUseCase
import com.yourssu.soomsil.usaint.domain.usecase.LecturesDiffUseCase
import com.yourssu.soomsil.usaint.domain.usecase.MakeSemesterFromLecturesUseCase
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
    private val uSaintSessionRepo: USaintSessionRepository,
    private val lectureRepo: LectureRepository,
    private val semesterRepo: SemesterRepository,
    private val lecturesDiffUseCase: LecturesDiffUseCase,
    private val getCurrentSemesterTypeUseCase: GetCurrentSemesterTypeUseCase,
    private val makeSemesterUseCase: MakeSemesterFromLecturesUseCase,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val currentSemester = getCurrentSemesterTypeUseCase() ?: return Result.success()
        // 작업 수행
        val session = uSaintSessionRepo.getSession().getOrElse { e ->
            Timber.e(e)
            return Result.failure()
        }
        val oldLectures = lectureRepo.getLocalLectures(currentSemester).getOrElse { e ->
            Timber.e(e)
            return Result.failure()
        }
        val newLectures = lectureRepo.getRemoteLectures(session, currentSemester).getOrElse { e ->
            Timber.e(e)
            return Result.failure()
        }
        val diffList = lecturesDiffUseCase(oldLectures, newLectures)

        if (diffList.isEmpty()) {
            if (BuildConfig.DEBUG) {
                // 디버그 용
                showNotification("디버그", "업데이트 된 성적이 없습니다.")
            }
            return Result.success()
        }

        for (lectureDiff in diffList) {
            // 각 변경사항에 대해 모두 알림 띄우기
            showNotification("성적 업데이트", "[${lectureDiff.title}] 성적이 업데이트 되었습니다.")
        }

        // 학기 정보 업데이트
        val newCurrentSemester = makeSemesterUseCase(currentSemester, newLectures)
        semesterRepo.storeSemesters(newCurrentSemester).onFailure { e ->
            Timber.e(e)
            return Result.failure()
        }
        // 강의 성적 정보 업데이트
        lectureRepo.storeLectures(*newLectures.toTypedArray()).onFailure { e ->
            Timber.e(e)
            return Result.failure()
        }

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
