package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.model.StudentInfoDto
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInfoDataStore
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import dev.eatsteak.rusaint.ffi.USaintSession
import timber.log.Timber
import javax.inject.Inject

class StudentInfoRepository @Inject constructor(
    private val studentInfoDataStore: StudentInfoDataStore,
    private val uSaintSessionRepo: USaintSessionRepository,
    private val rusaintApi: RusaintApi
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
        val stuInfo = rusaintApi.getStudentInformation(session).getOrElse { e ->
            Timber.e(e)
            return Result.failure(e)
        }
        return Result.success(
            StudentInfoDto(
                name = stuInfo.name,
                department = stuInfo.department,
                major = stuInfo.major,
                grade = stuInfo.grade,
                term = stuInfo.term,
            )
        )
    }

    suspend fun getStudentInfo(): Result<StudentInfoDto> {
        val session = uSaintSessionRepo.getSession().getOrElse { e ->
            return Result.failure(e)
        }
        return getStudentInfo(session)
    }
}