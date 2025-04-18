package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInfoDataStore
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import com.yourssu.soomsil.usaint.model.StudentInfoDto
import com.yourssu.soomsil.usaint.model.UserCredential
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

//    suspend fun getLocalStudentInfo(): Result<StudentInfoDto> {
//        return studentInfoDataStore.getStudentInfo()
//    }

    suspend fun storeUserCredential(userCredential: UserCredential): Result<Unit> {
        return studentInfoDataStore.setUserCredential(userCredential)
    }

//    suspend fun storeStudentInfo(studentInfo: StudentInfoDto): Result<Unit> {
//        return studentInfoDataStore.setStudentInfo(studentInfo)
//    }

    suspend fun getRemoteStudentInfo(session: USaintSession): Result<StudentInfoDto> {
        val graduationStudent = rusaintApi.getGraduationStudent(session).getOrElse { e ->
            Timber.e(e)
            return Result.failure(e)
        }
        val gradeSummary = rusaintApi.getCertificatedGradeSummary(session).getOrElse { e ->
            Timber.e(e)
            return Result.failure(e)
        }
        return Result.success(StudentInfoDto.from(graduationStudent, gradeSummary))
    }

    suspend fun deleteStudentInfo(): Result<Unit> {
        return studentInfoDataStore.deleteStudentInfo()
    }
}