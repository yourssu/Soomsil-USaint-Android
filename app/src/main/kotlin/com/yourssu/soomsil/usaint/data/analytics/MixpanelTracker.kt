package com.yourssu.soomsil.usaint.data.analytics

import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import javax.inject.Inject

class MixpanelTracker @Inject constructor(
    private val mixpanelAPI: MixpanelAPI,
    private val studentDataRepository: StudentDataRepository,
    private val chapelRepository: ChapelRepository,
) {

    fun trackAppLaunch() {
        mixpanelAPI.track("App Launch")
    }

    fun trackLogin(id: String) {
        val props = JSONObject().apply {
            put("schoolId", id)
        }
        mixpanelAPI.track("USER_LOGIN", props)
    }

    suspend fun trackCurrentSemesterClick() {
        studentDataRepository.studentData.first().let {
            val props = JSONObject().apply {
                put("department", it.department)
                put("schoolId", it.id)
                put("schoolYear", it.grade)
            }
            mixpanelAPI.track("THIS_SEMESTER_GRADE_CHECK Click", props)
        }
    }

    suspend fun trackNavigate(from: String, isChapel: Boolean) {
        val studentData = studentDataRepository.studentData.first()
        val chapelData = chapelRepository.chapelCard.first()

        val props = JSONObject().apply {
            put("department", studentData.department)
            put("schoolId", studentData.id)
            put("schoolYear", studentData.grade)
            put("chapel", chapelData != null)
            put("from", from)
        }
        if (isChapel) mixpanelAPI.track("CHAPEL_CHECK_CLICK", props)
        else mixpanelAPI.track("GRADE_CHECK_ALL_SEMESTER_CLICK", props)
    }

    suspend fun trackLectureDetail(lectureTitle: String) {
        studentDataRepository.studentData.first().let {
            val props = JSONObject().apply {
                put("department", it.department)
                put("schoolId", it.id)
                put("schoolYear", it.grade)
                put("lectureTitle", lectureTitle)
            }
            mixpanelAPI.track("GRADE_DETAIL_CHECK_CLICK", props)
        }
    }

    suspend fun trackSemester(semester: SemesterData) {
        studentDataRepository.studentData.first().let {
            val props = JSONObject().apply {
                put("department", it.department)
                put("schoolId", it.id)
                put("schoolYear", it.grade)
                put("semester", "${semester.year}년 ${semester.semester.kor}학기")
            }
            mixpanelAPI.track("GRADE_CHECK_SEMESTER_CLICK", props)
        }
    }

    suspend fun trackLogout() {
        studentDataRepository.studentData.first().let {
            val props = JSONObject().apply {
                put("department", it.department)
                put("schoolId", it.id)
                put("schoolYear", it.grade)
            }
            mixpanelAPI.track("USER_LOGOUT", props)
        }
    }

}