package com.yourssu.soomsil.usaint.data.source.remote.rusaint

import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.core.types.SemesterType
import dev.eatsteak.rusaint.core.ClassGrade
import dev.eatsteak.rusaint.core.CourseType
import dev.eatsteak.rusaint.core.GradeSummary
import dev.eatsteak.rusaint.core.GraduationStudent
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

/**
 * rusaint API와 1대1 대응되는 메서드를 제공하는 클래스
 */
class RusaintApi @Inject constructor() {

    @Volatile
    private var SESSION: USaintSession? = null
    private val mutex = Mutex()

    // 학번과 비밀번호로 인증된 세션
    // https://docs.rs/rusaint/latest/rusaint/struct.USaintSession.html#method.with_password
    private suspend fun getUSaintSession(credential: StudentCredential): USaintSession {
        return mutex.withLock {
            SESSION ?: USaintSessionBuilder()
                .withPassword(credential.id, credential.password)
                .also { SESSION = it }
        }
    }

    // 학적 정보 - 일반 학생 정보
    // https://docs.rs/rusaint/latest/rusaint/application/student_information/struct.StudentInformationApplication.html#method.general
    @Suppress("unused")
    suspend fun generalStudentInformation(credential: StudentCredential): StudentInformation {
        val session = getUSaintSession(credential)
        return StudentInformationApplicationBuilder().build(session).general()
    }

    // 증명 평점 정보
    // https://docs.rs/rusaint/latest/rusaint/application/course_grades/struct.CourseGradesApplication.html#method.certificated_summary
    suspend fun certificatedGradeSummary(credential: StudentCredential): GradeSummary {
        val session = getUSaintSession(credential)
        return CourseGradesApplicationBuilder().build(session)
            .certificatedSummary(CourseType.BACHELOR)
    }

    // 학기별 평점 정보
    // https://docs.rs/rusaint/latest/rusaint/application/course_grades/struct.CourseGradesApplication.html#method.semesters
    suspend fun semesterGradeList(credential: StudentCredential): List<SemesterGrade> {
        val session = getUSaintSession(credential)
        return CourseGradesApplicationBuilder().build(session).semesters(CourseType.BACHELOR)
    }

    // 주어진 학기의 수업별 성적
    // https://docs.rs/rusaint/latest/rusaint/application/course_grades/struct.CourseGradesApplication.html#method.classes
    suspend fun classGradeList(
        credential: StudentCredential,
        year: Int,
        semester: SemesterType,
    ): List<ClassGrade> {
        val session = getUSaintSession(credential)
        return CourseGradesApplicationBuilder().build(session)
            .classes(CourseType.BACHELOR, year.toUInt(), semester.toRusaintSemesterType(), false)
    }

    // 졸업사정표 - 학생 정보
    // https://docs.rs/rusaint/latest/rusaint/application/graduation_requirements/struct.GraduationRequirementsApplication.html#method.student_info
    suspend fun graduationStudentInformation(credential: StudentCredential): GraduationStudent {
        val session = getUSaintSession(credential)
        return GraduationRequirementsApplicationBuilder().build(session).studentInfo()
    }
}