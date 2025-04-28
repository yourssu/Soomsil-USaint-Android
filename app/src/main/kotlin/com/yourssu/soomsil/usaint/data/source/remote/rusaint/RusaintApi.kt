package com.yourssu.soomsil.usaint.data.source.remote.rusaint

import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.core.model.StudentData
import dev.eatsteak.rusaint.core.ClassGrade
import dev.eatsteak.rusaint.core.CourseType
import dev.eatsteak.rusaint.core.SemesterGrade
import dev.eatsteak.rusaint.core.StudentInformation
import dev.eatsteak.rusaint.ffi.CourseGradesApplicationBuilder
import dev.eatsteak.rusaint.ffi.GraduationRequirementsApplicationBuilder
import dev.eatsteak.rusaint.ffi.StudentInformationApplicationBuilder
import dev.eatsteak.rusaint.ffi.USaintSession
import dev.eatsteak.rusaint.ffi.USaintSessionBuilder
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class RusaintApi @Inject constructor() {

    @Volatile
    private var SESSION: USaintSession? = null
    private val mutex = Mutex()

    // 학번과 비밀번호로 인증된 세션
    // https://docs.rs/rusaint/latest/rusaint/struct.USaintSession.html#method.with_password
    internal suspend fun getUSaintSession(credential: StudentCredential): Result<USaintSession> {
        return runCatching {
            mutex.withLock {
                SESSION ?: USaintSessionBuilder().withPassword(credential.id, credential.password)
                    .also { SESSION = it }
            }
        }
    }

    // 일반 학생 정보
    // https://docs.rs/rusaint/latest/rusaint/application/student_information/struct.StudentInformationApplication.html#method.general
    suspend fun getStudentInformation(session: USaintSession): Result<StudentInformation> {
        return kotlin.runCatching {
            StudentInformationApplicationBuilder().build(session).general()
        }
    }

    // 전체 학기의 증명 평점 정보
    // https://docs.rs/rusaint/latest/rusaint/application/course_grades/struct.CourseGradesApplication.html#method.certificated_summary
    suspend fun getCertificatedGradeSummary(credential: StudentCredential): Result<ReportCardSummaryData> {
        return runCatching {
            val session = getUSaintSession(credential).getOrThrow()
            CourseGradesApplicationBuilder().build(session).certificatedSummary(CourseType.BACHELOR)
                .asExternalModel()
        }
    }

    // 졸업사정표 - 학생 정보
    // https://docs.rs/rusaint/latest/rusaint/application/graduation_requirements/struct.GraduationRequirementsApplication.html#method.student_info
    suspend fun getGraduationStudent(credential: StudentCredential): Result<StudentData> {
        return runCatching {
            val session = getUSaintSession(credential).getOrThrow()
            GraduationRequirementsApplicationBuilder().build(session).studentInfo()
                .asExternalModel()
        }
    }

    // 학기별 평점 정보
    // https://docs.rs/rusaint/latest/rusaint/application/course_grades/struct.CourseGradesApplication.html#method.semesters
    suspend fun getSemesterGradeList(session: USaintSession): Result<List<SemesterGrade>> {
        return kotlin.runCatching {
            CourseGradesApplicationBuilder().build(session).semesters(CourseType.BACHELOR)
        }
    }

    // 주어진 학기의 수업별 성적
    // https://docs.rs/rusaint/latest/rusaint/application/course_grades/struct.CourseGradesApplication.html#method.classes
    suspend fun getClassGradeList(
        session: USaintSession,
        year: UInt,
        semester: RusaintSemesterType
    ): Result<List<ClassGrade>> {
        return kotlin.runCatching {
            CourseGradesApplicationBuilder().build(session)
                .classes(CourseType.BACHELOR, year, semester, false)
        }
    }
}