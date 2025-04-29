package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.datastore.ReportCardSummaryDataSource
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.SortedMap
import javax.inject.Inject

class ReportCardRepository @Inject constructor(
    private val reportCardSummaryDataSource: ReportCardSummaryDataSource,
    private val semesterDao: SemesterDao,
) {
    val reportCardSummaryData: Flow<ReportCardSummaryData> =
        reportCardSummaryDataSource.reportCardSummaryData

    val semesterWithLectures: Flow<Map<SemesterData, List<LectureData>>> =
        semesterDao.getSemesterWithLectures().map { semesterEntityListMap ->
            semesterEntityListMap
                .mapKeys { (semesterEntity, _) -> semesterEntity.asExternalModel() }
                .mapValues { (_, lectureEntity) -> lectureEntity.map(LectureEntity::asExternalModel) }
        }

    suspend fun setReportCardData(reportCardSummaryData: ReportCardSummaryData) =
        reportCardSummaryDataSource.setReportCardData(reportCardSummaryData)
}