package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.model.StudentInfoDto
import dev.eatsteak.rusaint.ffi.StudentInformationApplicationBuilder
import dev.eatsteak.rusaint.ffi.USaintSession
import javax.inject.Inject

class StudentInfoRepository @Inject constructor(
    private val uSaintSessionRepo: USaintSessionRepository,
) {
    suspend fun getStudentInfo(session: USaintSession): Result<StudentInfoDto> {
        return kotlin.runCatching {
            val studentInfo = StudentInformationApplicationBuilder().build(session).general()
            StudentInfoDto(
                name = studentInfo.name,
                department = studentInfo.department,
                major = studentInfo.major,
                grade = studentInfo.grade,
                term = studentInfo.term,
            )
        }
    }

    suspend fun getStudentInfo(): Result<StudentInfoDto> {
        val session = uSaintSessionRepo.getSession().getOrElse { e ->
            return Result.failure(e)
        }
        return getStudentInfo(session)
    }
}