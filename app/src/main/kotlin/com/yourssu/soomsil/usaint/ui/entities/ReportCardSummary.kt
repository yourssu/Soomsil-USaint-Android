package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable

@Immutable
data class ReportCardSummary(
    val gpa: Grade = Grade.Zero,
    val earnedCredit: Credit = Credit.Zero,
    val graduateCredit: Credit = Credit.Zero,
)
