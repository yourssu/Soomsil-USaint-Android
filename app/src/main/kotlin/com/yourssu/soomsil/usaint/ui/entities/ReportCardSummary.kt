package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCardVO

@Immutable
data class ReportCardSummary(
    val gpa: Grade = Grade.Zero,
    val earnedCredit: Credit = Credit.Zero,
    val graduateCredit: Credit = Credit.Zero,
)

fun TotalReportCardVO.toReportCardSummary() = ReportCardSummary(
    gpa = gpa.toGrade(),
    earnedCredit = earnedCredit.toCredit(),
    graduateCredit = graduateCredit.toCredit(),
)