package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.datastore.ReportCardSummaryDataSource
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.asEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.asExternalModel
import com.yourssu.soomsil.usaint.data.source.remote.USaintRemoteSource
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class ReportCardRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val reportCardSummary: ReportCardSummaryDataSource,
    private val semesterDao: SemesterDao,
    private val lectureDao: LectureDao,
    private val uSaintRemoteSource: USaintRemoteSource,
    private val getCurrentSemesterUseCase: GetCurrentSemesterUseCase,
) {
    val reportCardSummaryData: Flow<ReportCardSummaryData> =
        reportCardSummary.reportCardSummaryData

    val semesterWithLectures: Flow<Map<SemesterData, List<LectureData>>> =
        semesterDao.getSemesterWithLectures().map { semesterEntityListMap ->
            semesterEntityListMap
                .mapKeys { (semesterEntity, _) -> semesterEntity.asExternalModel() }
                .mapValues { (_, lectureEntity) -> lectureEntity.map(LectureEntity::asExternalModel) }
        }

    // 전체 학기 목록(학기별 GPA 포함). Semester 테이블은 한 번에 채워지므로
    // 강의 적재를 기다리는 semesterWithLectures와 달리 즉시 전체가 로드된다.
    val semesters: Flow<List<SemesterData>> =
        semesterDao.getSemesterEntities().map { entities ->
            entities.map(SemesterEntity::asExternalModel)
        }

    suspend fun fetchReportCardSummary(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val reportCardSummaryData = uSaintRemoteSource.remoteReportCardSummaryData(credential)
        reportCardSummary.setReportCardData(reportCardSummaryData)
    }

    suspend fun fetchCurrentSemesterLectures(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val currentSemester = getCurrentSemesterUseCase() ?: return@runCatching

        val lectureDataList = uSaintRemoteSource.remoteLectureDataList(
            credential,
            currentSemester.first,
            currentSemester.second
        )

        lectureDao.upsertLectures(lectureDataList.map(LectureData::asEntity))

    }

    suspend fun fetchSemesterWithLectures(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val semesterDataList = uSaintRemoteSource.remoteSemesterDataList(credential)
        semesterDao.upsertSemesters(semesterDataList.map(SemesterData::asEntity))

        for (semesterData in semesterDataList) {
            Timber.d("semesterData: $semesterData")
            val lectureDataList = uSaintRemoteSource.remoteLectureDataList(
                credential,
                semesterData.year,
                semesterData.semester
            )
            lectureDao.upsertLectures(lectureDataList.map(LectureData::asEntity))
        }
    }

    suspend fun deleteAll() {
        // semester를 삭제할 경우 연관된 lecture까지 삭제됩니다 (Cascade)
        semesterDao.deleteAllSemesters()
        reportCardSummary.clear()
    }
}