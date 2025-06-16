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
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import com.yourssu.soomsil.usaint.domain.usecase.LecturesDiffUseCase
import com.yourssu.soomsil.usaint.domain.usecase.MakeSemesterUseCase
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
    private val getCurrentSemesterUseCase: GetCurrentSemesterUseCase,
    private val makeSemesterUseCase: MakeSemesterUseCase,
    private val lecturesDiffUseCase: LecturesDiffUseCase,
    private val lectureRepository: LectureRepository,
    private val semesterRepository: SemesterRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        // TODO: UseCase로 분리하기
        val currentSemester = getCurrentSemesterUseCase() ?: return Result.success()
        val oldLectures = lectureRepository.getLocalLectures(currentSemester.first, currentSemester.second).getOrElse {
            //성적에 과목 반영이 안 되어있을 때 로컬에는 기존 lecture가 없음 -> 원격에서는 가져오기에
            emptyList()
        }
        val newLectures = lectureRepository.getRemoteLectures(currentSemester.first, currentSemester.second).getOrElse { e ->
            Timber.e(e)
            return Result.failure()
        }

        val newCurrentSemester = makeSemesterUseCase(currentSemester, newLectures)
        if(oldLectures.isNotEmpty()) {
            val diffList = lecturesDiffUseCase(oldLectures, newLectures)

            if (diffList.isEmpty()) {
                if (BuildConfig.DEBUG) {
                    // 디버그 용
                    showNotification("디버그", "업데이트 된 성적이 없습니다.")
                }
                return Result.success()
            }

            diffList.forEach { lectureDiff ->
                // 각 변경사항에 대해 모두 알림 띄우기
                showNotification("성적 업데이트", "[${lectureDiff.title}] 성적이 업데이트 되었습니다.")
            }
        }

//         fixme #44
//         학기 정보 업데이트
        //val newCurrentSemester = makeSemesterUseCase(currentSemester, newLectures)
        semesterRepository.storeSemesters(newCurrentSemester).onFailure { e ->
            Timber.e(e)
            return Result.failure()
        }
        // 강의 성적 정보 업데이트
        lectureRepository.storeLectures(*newLectures.toTypedArray()).onFailure { e ->
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
