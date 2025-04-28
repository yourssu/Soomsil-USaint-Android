package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.data.source.local.datastore.ReportCardSummaryDataSource
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInformationDataSource
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentDataRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val studentInformation: StudentInformationDataSource,
    private val reportCardSummary: ReportCardSummaryDataSource,
    private val rusaintApi: RusaintApi,
) {
    val studentData: Flow<StudentData> = studentInformation.studentData

    suspend fun setStudentData(studentData: StudentData) =
        studentInformation.setStudentData(studentData)

    suspend fun fetchStudentData(): Result<Unit> {
        return runCatching {
            val credential = studentCredential.getStudentCredential()
            // TODO pass custom error
            val studentData = rusaintApi.getGraduationStudent(credential).getOrThrow()
            studentInformation.setStudentData(studentData)
            val reportCardData = rusaintApi.getCertificatedGradeSummary(credential).getOrThrow()
            reportCardSummary.setReportCardData(reportCardData)
        }
    }
}