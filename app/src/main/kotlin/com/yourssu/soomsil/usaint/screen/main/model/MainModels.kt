package com.yourssu.soomsil.usaint.screen.main.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp

data class GpaBarData(
    val label: String,
    val height: Dp,
    val isCurrent: Boolean = false,
    val gpaText: String? = null
)

data class TabItem(
    val label: String,
    @DrawableRes val iconRes: Int
)

// 이번 학기 성적 바텀시트의 강의 항목
data class SemesterCourseItem(
    val name: String,
    val professor: String,
    val credit: String,
    val grade: String,
)
