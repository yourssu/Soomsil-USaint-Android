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
