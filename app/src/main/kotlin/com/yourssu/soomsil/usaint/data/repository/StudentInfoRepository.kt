package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.model.StudentInfoDto
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInfoDataStore
import dev.eatsteak.rusaint.ffi.StudentInformationApplicationBuilder
import dev.eatsteak.rusaint.ffi.USaintSession
import javax.inject.Inject

class StudentInfoRepository @Inject constructor(
    private val studentInfoDataStore: StudentInfoDataStore,
    private val uSaintSessionRepo: USaintSessionRepository,
) {
    suspend fun getPasswordFromDataStore(): Result<Pair<String, String>> {
        return studentInfoDataStore.getPassword()
    }

    suspend fun getStudentInfoFromDataStore(): Result<StudentInfoDto> {
        return studentInfoDataStore.getStudentInfo()
    }

    suspend fun storePassword(id: String, pw: String): Result<Unit> {
        return studentInfoDataStore.setPassword(id, pw)
    }

    suspend fun storeStudentInfo(studentInfo: StudentInfoDto): Result<Unit> {
        return studentInfoDataStore.setStudentInfo(studentInfo)
    }

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