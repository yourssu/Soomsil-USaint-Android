package com.yourssu.soomsil.usaint.data.analytics

import com.posthog.PostHog
import com.posthog.android.PostHogAndroid
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import javax.inject.Inject

class PosthogTracker @Inject constructor(
    private val studentDataRepository: StudentDataRepository,
    private val chapelRepository: ChapelRepository,
) {
    fun trackAppLaunch() {
        PostHog.capture(event = "app_launched")
    }

    fun trackLogin(id: String) {
        PostHog.capture(
            event = "USER_LOGIN",
            properties = mapOf(
                "schoolId" to id,
            )
        )
    }

    suspend fun trackCurrentSemesterClick() {
        studentDataRepository.studentData.first().let {
            PostHog.capture(
                event = "THIS_SEMESTER_GRADE_CHECK",
                properties = mapOf(
                    "department" to it.department,
                    "schoolId" to it.id,
                    "schoolYear" to it.applyYear,
                )
            )
        }
    }

    suspend fun trackNavigate(from: String, isChapel: Boolean) {
        val studentData = studentDataRepository.studentData.first()
        val chapelData = chapelRepository.chapelCard.first()

        val props = mapOf(
            "department" to studentData.department,
            "schoolId" to studentData.id,
            "schoolYear" to studentData.applyYear,
            "chapel" to (chapelData != null),
            "from" to from,
        )
        if (isChapel) PostHog.capture(event = "CHAPEL_CHECK_CLICK", properties = props)
        else PostHog.capture(event = "GRADE_CHECK_ALL_SEMESTER_CLICK", properties = props)
    }

    suspend fun trackLectureDetail(lectureTitle: String) {
        studentDataRepository.studentData.first().let {
            val props = mapOf(
                "department" to it.department,
                "schoolId" to it.id,
                "schoolYear" to it.applyYear,
                "lectureTitle" to lectureTitle,
            )
            PostHog.capture(event = "GRADE_DETAIL_CHECK_CLICK", properties = props)
        }
    }

    suspend fun trackSemester(semester: SemesterData) {
        studentDataRepository.studentData.first().let {
            val props = mapOf(
                "department" to it.department,
                "schoolId" to it.id,
                "schoolYear" to it.applyYear,
                "semester" to "${semester.year}년 ${semester.semester.kor}학기",
            )
            PostHog.capture(event = "GRADE_CHECK_SEMESTER_${semester.semester.kor}_CLICK", properties = props)
        }
    }

    suspend fun trackLogout() {
        studentDataRepository.studentData.first().let {
            val props = mapOf(
                "department" to it.department,
                "schoolId" to it.id,
                "schoolYear" to it.applyYear,
            )
            PostHog.capture(event = "USER_LOGOUT", properties = props)
        }
    }

    suspend fun trackAutoLoadClick() {
        studentDataRepository.studentData.first().let {
            val props = mapOf(
                "department" to it.department,
                "schoolId" to it.id,
                "schoolYear" to it.applyYear,
            )
            PostHog.capture(event = "LATEST_ACADEMIC_INFO_AUTOLOAD_CLICK", properties = props)
        }
    }

}