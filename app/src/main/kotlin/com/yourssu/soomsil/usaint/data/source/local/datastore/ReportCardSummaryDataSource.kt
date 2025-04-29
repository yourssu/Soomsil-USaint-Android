package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.proto.ReportCardSummaryProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ReportCardSummaryDataSource @Inject constructor(
    private val reportCardSummaryDataSource: DataStore<ReportCardSummaryProto>,
) {
    val reportCardSummaryData: Flow<ReportCardSummaryData> = reportCardSummaryDataSource.data
        .map {
            ReportCardSummaryData(
                attemptedCredits = it.attemptedCredits,
                earnedCredits = it.earnedCredits,
                gradePointsSum = it.gradePointsSum,
                gradePointsAverage = it.gradePointsAverage,
                arithmeticMean = it.arithmeticMean,
                pfEarnedCredits = it.pfEarnedCredits,
                graduationPoints = it.graduationPoints,
                completedPoints = it.completedPoints,
            )
        }

    suspend fun setReportCardData(summaryData: ReportCardSummaryData) {
        try {
            reportCardSummaryDataSource.updateData {
                ReportCardSummaryProto.newBuilder()
                    .setAttemptedCredits(summaryData.attemptedCredits)
                    .setEarnedCredits(summaryData.earnedCredits)
                    .setGradePointsSum(summaryData.gradePointsSum)
                    .setGradePointsAverage(summaryData.gradePointsAverage)
                    .setArithmeticMean(summaryData.arithmeticMean)
                    .setPfEarnedCredits(summaryData.pfEarnedCredits)
                    .setGraduationPoints(summaryData.graduationPoints)
                    .setCompletedPoints(summaryData.completedPoints)
                    .build()
            }
        } catch (e: IOException) {
            Timber.e("Failed to update report card data", e)
        }
    }
}