package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable

@Immutable
data class TotalReportCardInfo(
    val gpa: Grade,
    val earnedCredit: Credit,
    val graduateCredit: Credit
)
