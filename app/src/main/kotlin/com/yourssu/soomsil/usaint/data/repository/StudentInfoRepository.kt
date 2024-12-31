package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.model.StudentInfoDto
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInfoDataStore
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import com.yourssu.soomsil.usaint.domain.type.UserCredential
import dev.eatsteak.rusaint.ffi.USaintSession
import timber.log.Timber
import javax.inject.Inject

class StudentInfoRepository @Inject constructor(
    private val studentInfoDataStore: StudentInfoDataStore,
    private val rusaintApi: RusaintApi
) {
    suspend fun getLocalUserCredential(): Result<UserCredential> {
        return studentInfoDataStore.getUserCredential()
    }

    suspend fun getLocalStudentInfo(): Result<StudentInfoDto> {
        return studentInfoDataStore.getStudentInfo()
    }

    suspend fun storeUserCredential(userCredential: UserCredential): Result<Unit> {
        return studentInfoDataStore.setUserCredential(userCredential)
    }

    suspend fun storeStudentInfo(studentInfo: StudentInfoDto): Result<Unit> {
        return studentInfoDataStore.setStudentInfo(studentInfo)
    }

    suspend fun getRemoteStudentInfo(session: USaintSession): Result<StudentInfoDto> {
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

    suspend fun deleteStudentInfo(): Result<Unit> {
        return studentInfoDataStore.deleteStudentInfo()
    }
}