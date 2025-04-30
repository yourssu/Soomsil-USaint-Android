package com.yourssu.soomsil.usaint.data.source.remote

import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.asExternalModel
import javax.inject.Inject

class USaintRemoteSource @Inject constructor(
    private val rusaintApi: RusaintApi,
) {
    suspend fun remoteStudentData(credential: StudentCredential): StudentData {
        val graduationStudent = rusaintApi.graduationStudentInformation(credential)
        return graduationStudent.asExternalModel()
    }

    suspend fun remoteReportCardSummaryData(credential: StudentCredential): ReportCardSummaryData {
        val graduationStudent = rusaintApi.graduationStudentInformation(credential)
        val gradeSummary = rusaintApi.certificatedGradeSummary(credential)
        return gradeSummary.asExternalModel(
            graduationPoints = graduationStudent.graduationPoints,
            completedPoints = graduationStudent.completedPoints,
        )
    }
}