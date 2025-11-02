package com.yourssu.soomsil.usaint.core.model

data class ReportCardSummaryData(
    val attemptedCredits: Float,
    val earnedCredits: Float,
    val gradePointsSum: Float,
    val gradePointsAverage: Float,
    val arithmeticMean: Float,
    val pfEarnedCredits: Float,
    val graduationPoints: Float,// 졸업학점
    val completedPoints: Float, // 인정학점
) {
    companion object {
        val previewData = ReportCardSummaryData(
            attemptedCredits = 97f,
            earnedCredits = 97f,
            gradePointsSum = 123f,
            gradePointsAverage = 4.22f,
            arithmeticMean = 100f,
            pfEarnedCredits = 5f,
            graduationPoints = 133f,
            completedPoints = 97f,
        )
    }
}