package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.datastore.ReportCardSummaryDataSource
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.asEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.asExternalModel
import com.yourssu.soomsil.usaint.data.source.remote.USaintRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReportCardRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val reportCardSummary: ReportCardSummaryDataSource,
    private val semesterDao: SemesterDao,
    private val lectureDao: LectureDao,
    private val uSaintRemoteSource: USaintRemoteSource,
) {
    val reportCardSummaryData: Flow<ReportCardSummaryData> =
        reportCardSummary.reportCardSummaryData

    val semesterWithLectures: Flow<Map<SemesterData, List<LectureData>>> =
        semesterDao.getSemesterWithLectures().map { semesterEntityListMap ->
            semesterEntityListMap
                .mapKeys { (semesterEntity, _) -> semesterEntity.asExternalModel() }
                .mapValues { (_, lectureEntity) -> lectureEntity.map(LectureEntity::asExternalModel) }
        }

    suspend fun fetchReportCardSummary(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val reportCardSummaryData = uSaintRemoteSource.remoteReportCardSummaryData(credential)
        reportCardSummary.setReportCardData(reportCardSummaryData)
    }

    suspend fun fetchSemesterWithLectures(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val semesterDataList = uSaintRemoteSource.remoteSemesterDataList(credential)
        semesterDao.upsertSemesters(semesterDataList.map(SemesterData::asEntity))

        for (semesterData in semesterDataList) {
            val lectureDataList = uSaintRemoteSource.remoteLectureDataList(
                credential,
                semesterData.year,
                semesterData.semester
            )
            lectureDao.upsertLectures(lectureDataList.map(LectureData::asEntity))
        }
    }
}