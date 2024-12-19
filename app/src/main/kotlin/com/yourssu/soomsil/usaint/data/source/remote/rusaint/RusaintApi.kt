package com.yourssu.soomsil.usaint.data.source.remote.rusaint

import dev.eatsteak.rusaint.core.CourseType
import dev.eatsteak.rusaint.core.GradeSummary
import dev.eatsteak.rusaint.core.GraduationStudent
import dev.eatsteak.rusaint.core.StudentInformation
import dev.eatsteak.rusaint.ffi.CourseGradesApplicationBuilder
import dev.eatsteak.rusaint.ffi.GraduationRequirementsApplicationBuilder
import dev.eatsteak.rusaint.ffi.StudentInformationApplicationBuilder
import dev.eatsteak.rusaint.ffi.USaintSession
import dev.eatsteak.rusaint.ffi.USaintSessionBuilder
import javax.inject.Inject

class RusaintApi @Inject constructor() {

    // 학번과 비밀번호로 인증된 세션
    // https://docs.rs/rusaint/latest/rusaint/struct.USaintSession.html#method.with_password
    suspend fun getUSaintSession(id: String, pw: String): Result<USaintSession> {
        return kotlin.runCatching {
            USaintSessionBuilder().withPassword(id, pw)
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
    suspend fun getCertificatedGradeSummary(session: USaintSession): Result<GradeSummary> {
        return kotlin.runCatching {
            CourseGradesApplicationBuilder().build(session).certificatedSummary(CourseType.BACHELOR)
        }
    }

    // 졸업사정표 - 학생 정보
    // https://docs.rs/rusaint/latest/rusaint/application/graduation_requirements/struct.GraduationRequirementsApplication.html#method.student_info
    suspend fun getGraduationStudentInfo(session: USaintSession): Result<GraduationStudent> {
        return kotlin.runCatching {
            GraduationRequirementsApplicationBuilder().build(session).studentInfo()
        }
    }
}