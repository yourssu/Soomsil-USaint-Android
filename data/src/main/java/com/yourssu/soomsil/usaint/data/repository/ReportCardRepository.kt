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
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.source.local.datastore.UserPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class ReportCardRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val reportCardSummary: ReportCardSummaryDataSource,
    private val semesterDao: SemesterDao,
    private val lectureDao: LectureDao,
    private val uSaintRemoteSource: USaintRemoteSource,
    private val userPreferencesDataSource: UserPreferencesDataSource,
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

    suspend fun fetchCurrentSemesterLectures(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val currentSemester = getCurrentSemester() ?: return@runCatching

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

    /**
     * 현재 학기를 가져옵니다.
     * GetCurrentSemesterUseCase와 동일한 로직이지만 순환 의존성을 피하기 위해 여기에 구현합니다.
     */
    private suspend fun getCurrentSemester(): Pair<Int, SemesterType>? {
        val userData = userPreferencesDataSource.userData.first()
        if (userData.isCurrentSemesterSpecified) return userData.specifiedCurrentSemester
        return getDefaultCurrentSemester()
    }

    private fun getDefaultCurrentSemester(): Pair<Int, SemesterType>? {
        val now = LocalDate.now()
        val year = now.year

        // 2025년도 학기 개강 ~ 종강
        // https://ssu.ac.kr/%ED%95%99%EC%82%AC/%ED%95%99%EC%82%AC%EC%9D%BC%EC%A0%95/
        // 1학기: 3/4 ~ 6/23 (성적 처리기간 ~7.7)
        // 여름학기: 6/24 ~ 7/14 (성적 처리기간 ~7.31)
        // 2학기: 9/1 ~ 12/20 (성적 처리기간 ~1.7)
        // 겨울학기: 12/22 ~ 1/15 (성적 처리기간 ~1.31)
        return when (now) {
            in LocalDate.of(year, 3, 4)..LocalDate.of(year, 7, 7) ->
                Pair(year, SemesterType.One)

            in LocalDate.of(year, 7, 8)..LocalDate.of(year, 7, 14) ->
                Pair(year, SemesterType.Summer)

            in LocalDate.of(year, 9, 1)..LocalDate.of(year, 12, 31) ->
                Pair(year, SemesterType.Two)

            in LocalDate.of(year, 1, 1)..LocalDate.of(year, 1, 7) ->
                Pair(year-1, SemesterType.Two)

            in LocalDate.of(year, 1, 8)..LocalDate.of(year, 1, 31) ->
                Pair(year-1, SemesterType.Winter)

            else -> null
        }
    }
}