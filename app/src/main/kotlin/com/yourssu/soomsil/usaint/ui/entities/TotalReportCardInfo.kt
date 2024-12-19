package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable

@Immutable
data class TotalReportCardInfo(
    val gpa: Grade = Grade.Zero,
    val earnedCredit: Credit = Credit.Zero,
    val graduateCredit: Credit = Credit.Zero,
)
