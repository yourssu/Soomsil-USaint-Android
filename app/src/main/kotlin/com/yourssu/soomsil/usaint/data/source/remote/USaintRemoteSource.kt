package com.yourssu.soomsil.usaint.data.source.remote

import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.asExternalModel
import dev.eatsteak.rusaint.core.SemesterGrade
import javax.inject.Inject

/**
 * rusaint API를 사용하여 core model 타입으로 반환하는 클래스입니다
 */
class USaintRemoteSource @Inject constructor(
    private val rusaintApi: RusaintApi,
) {
    suspend fun remoteStudentData(credential: StudentCredential): StudentData {
        val graduationStudent = rusaintApi.graduationStudentInformation(credential)
        return graduationStudent.asExternalModel()
    }

    suspend fun     remoteReportCardSummaryData(credential: StudentCredential): ReportCardSummaryData {
        val graduationStudent = rusaintApi.graduationStudentInformation(credential)
        val gradeSummary = rusaintApi.certificatedGradeSummary(credential)
        return gradeSummary.asExternalModel(
            graduationPoints = graduationStudent.graduationPoints,
            completedPoints = graduationStudent.completedPoints,
        )
    }

    suspend fun remoteSemesterDataList(credential: StudentCredential): List<SemesterData> {
        val semesterGradeList = rusaintApi.semesterGradeList(credential)
        return semesterGradeList.map(SemesterGrade::asExternalModel)
    }

    suspend fun remoteLectureDataList(
        credential: StudentCredential,
        year: Int,
        semester: SemesterType,
    ): List<LectureData> {
        val classGradeList = rusaintApi.classGradeList(credential, year, semester)
        return classGradeList.map { it.asExternalModel(year, semester) }
    }

    suspend fun remoteChapelData(
        credential: StudentCredential,
        year: Int,
        semester: SemesterType,
    ): ChapelData {
        val chapelInformation = rusaintApi.chapelInformation(credential, year, semester)
        return chapelInformation.asExternalModel(year,semester)
    }
}