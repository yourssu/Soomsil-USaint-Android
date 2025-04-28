package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.data.source.local.datastore.ReportCardSummaryDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportCardRepository @Inject constructor(
    private val reportCardSummaryDataSource: ReportCardSummaryDataSource,
) {
    val reportCardSummaryData: Flow<ReportCardSummaryData> = reportCardSummaryDataSource.reportCardSummaryData

    suspend fun setReportCardData(reportCardSummaryData: ReportCardSummaryData) =
        reportCardSummaryDataSource.setReportCardData(reportCardSummaryData)
}