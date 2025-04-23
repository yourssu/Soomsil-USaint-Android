package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInformationDataSource
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentDataRepository @Inject constructor(
    private val studentInformation: StudentInformationDataSource,
    private val rusaintApi: RusaintApi
) {
    val studentData: Flow<StudentData> = studentInformation.studentData

    suspend fun setStudentData(studentData: StudentData) =
        studentInformation.setStudentData(studentData)

    // TODO
//    suspend fun getRemoteStudentInfo(session: USaintSession): Result<StudentInfoDto> {
//        val graduationStudent = rusaintApi.getGraduationStudent(session).getOrElse { e ->
//            Timber.e(e)
//            return Result.failure(e)
//        }
//        val gradeSummary = rusaintApi.getCertificatedGradeSummary(session).getOrElse { e ->
//            Timber.e(e)
//            return Result.failure(e)
//        }
//        return Result.success(StudentInfoDto.from(graduationStudent, gradeSummary))
//    }
}