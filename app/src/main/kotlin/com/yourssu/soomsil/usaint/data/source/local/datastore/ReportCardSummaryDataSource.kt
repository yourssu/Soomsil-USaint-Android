package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import com.yourssu.soomsil.usaint.core.model.ReportCardData
import com.yourssu.soomsil.usaint.proto.ReportCardSummaryProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ReportCardSummaryDataSource @Inject constructor(
    private val reportCardSummaryDataSource: DataStore<ReportCardSummaryProto>,
) {
    val reportCardSummary: Flow<ReportCardData> = reportCardSummaryDataSource.data
        .map {
            ReportCardData(
                attemptedCredits = it.attemptedCredits,
                earnedCredits = it.earnedCredits,
                gradePointsSum = it.gradePointsSum,
                gradePointsAverage = it.gradePointsAverage,
                arithmeticMean = it.arithmeticMean,
                pfEarnedCredits = it.pfEarnedCredits,
            )
        }

    suspend fun setReportCardData(reportCardData: ReportCardData) {
        try {
            reportCardSummaryDataSource.updateData {
                ReportCardSummaryProto.newBuilder()
                    .setAttemptedCredits(reportCardData.attemptedCredits)
                    .setEarnedCredits(reportCardData.earnedCredits)
                    .setGradePointsSum(reportCardData.gradePointsSum)
                    .setGradePointsAverage(reportCardData.gradePointsAverage)
                    .setArithmeticMean(reportCardData.arithmeticMean)
                    .setPfEarnedCredits(reportCardData.pfEarnedCredits)
                    .build()
            }
        } catch (e: IOException) {
            Timber.e("Failed to update report card data", e)
        }
    }
}