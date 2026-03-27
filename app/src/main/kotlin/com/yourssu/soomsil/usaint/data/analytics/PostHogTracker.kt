package com.yourssu.soomsil.usaint.data.analytics

import com.posthog.PostHog
import com.yourssu.soomsil.usaint.BuildConfig
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PostHogTracker @Inject constructor(
    private val studentDataRepository: StudentDataRepository,
    private val chapelRepository: ChapelRepository,
) {
    fun trackAppLaunch() {
        val props = mapOf(
            "platform" to "android",
            "app_version" to BuildConfig.VERSION_NAME,
        )
        PostHog.capture(
            event = "app_launched",
            userProperties = props
        )
    }

    suspend fun trackLogin(id: String) {
        studentDataRepository.studentData.first().let {
            val props = mapOf(
                "department" to it.department,
                "grade_year" to it.grade,
                "status" to it.status
            )
            PostHog.identify(
                distinctId = id,
                userProperties = props
            )
        }
    }

    fun trackLoginFailed(e: Throwable) {
        val props = mapOf(
            "fail_reason" to e.localizedMessage,
            //"fail_stack" to e.stackTraceToString()
        )
        PostHog.capture(
            event = "login_failed",
            properties = props
        )
    }

    fun trackHomeViewed() {
        val props = mapOf(
            "screen_name" to "home"
        )
        PostHog.capture(
            event = "home_viewed",
            userProperties = props
        )
    }


    suspend fun trackCurrentSemesterClick() {
//        studentDataRepository.studentData.first().let {
//            PostHog.capture(
//                event = "THIS_SEMESTER_GRADE_CHECK",
//                properties = mapOf(
//                    "department" to it.department,
//                    "schoolId" to it.id,
//                    "schoolYear" to it.applyYear,
//                )
//            )
//        }
    }

    suspend fun trackNavigate(from: String, isChapel: Boolean) {
//        val studentData = studentDataRepository.studentData.first()
//        val chapelData = chapelRepository.chapelCard.first()
//
//        val props = mapOf(
//            "department" to studentData.department,
//            "schoolId" to studentData.id,
//            "schoolYear" to studentData.applyYear,
//            "chapel" to (chapelData != null),
//            "from" to from,
//        )
//        if (isChapel) PostHog.capture(event = "CHAPEL_CHECK_CLICK", properties = props)
//        else PostHog.capture(event = "GRADE_CHECK_ALL_SEMESTER_CLICK", properties = props)
    }

    suspend fun trackLectureDetail(lectureTitle: String) {
//        studentDataRepository.studentData.first().let {
//            val props = mapOf(
//                "department" to it.department,
//                "schoolId" to it.id,
//                "schoolYear" to it.applyYear,
//                "lectureTitle" to lectureTitle,
//            )
//            PostHog.capture(event = "GRADE_DETAIL_CHECK_CLICK", properties = props)
//        }
    }

    suspend fun trackSemester(semester: SemesterData) {
//        studentDataRepository.studentData.first().let {
//            val props = mapOf(
//                "department" to it.department,
//                "schoolId" to it.id,
//                "schoolYear" to it.applyYear,
//                "semester" to "${semester.year}년 ${semester.semester.kor}학기",
//            )
//            PostHog.capture(event = "GRADE_CHECK_SEMESTER_${semester.semester.kor}_CLICK", properties = props)
//        }
    }

    fun trackLogout() {
        PostHog.capture(event = "logout_clicked")
    }

    fun trackViewTermsOfUse() {
        PostHog.capture(event = "terms_viewed")
    }

    fun trackViewPrivacy() {
        PostHog.capture(event = "privacy_policy_viewed")
    }


    suspend fun trackAutoLoadClick() {
//        studentDataRepository.studentData.first().let {
//            val props = mapOf(
//                "department" to it.department,
//                "schoolId" to it.id,
//                "schoolYear" to it.applyYear,
//            )
//            PostHog.capture(event = "LATEST_ACADEMIC_INFO_AUTOLOAD_CLICK", properties = props)
//        }
    }

    fun trackSettingView() {
        val props = mapOf(
            "screen_name" to "settings"
        )
        PostHog.capture(
            event = "settings_viewed",
            userProperties = props
        )
    }

}