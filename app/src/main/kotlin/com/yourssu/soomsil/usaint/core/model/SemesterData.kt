package com.yourssu.soomsil.usaint.core.model

import com.yourssu.soomsil.usaint.core.types.SemesterType

/**
 * 학기 상세 정보
 */
data class SemesterData(
    val year: Int,
    val semester: SemesterType,
    val gradePointsAverage: Float,
    val attemptedCredit: Float,
    val earnedCredit: Float,
    val pfEarnedCredit: Float,
    val semesterRank: Pair<Int, Int>,   // Pair<rank, total count>
    val generalRank: Pair<Int, Int>,    // Pair<rank, total count>
)
