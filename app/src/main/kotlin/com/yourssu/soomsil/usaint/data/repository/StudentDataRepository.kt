package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.data.source.local.datastore.ReportCardSummaryDataSource
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInformationDataSource
import com.yourssu.soomsil.usaint.data.source.remote.USaintRemoteSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentDataRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val studentInformation: StudentInformationDataSource,
    private val reportCardSummary: ReportCardSummaryDataSource,
    private val uSaintRemoteSource: USaintRemoteSource,
) {
    val studentData: Flow<StudentData> = studentInformation.studentData

    suspend fun setStudentData(studentData: StudentData) =
        studentInformation.setStudentData(studentData)

    suspend fun fetchStudentData(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val studentData = uSaintRemoteSource.remoteStudentData(credential)
        studentInformation.setStudentData(studentData)
        val reportCardSummaryData = uSaintRemoteSource.remoteReportCardSummaryData(credential)
        reportCardSummary.setReportCardData(reportCardSummaryData)
    }

    suspend fun clear() = studentInformation.clear()
}