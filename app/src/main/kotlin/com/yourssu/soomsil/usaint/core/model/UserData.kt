package com.yourssu.soomsil.usaint.core.model

import com.yourssu.soomsil.usaint.core.types.SemesterType

data class UserData(
    val notificationEnabled: Boolean,
    val includeSeasonalSemester: Boolean,
    val isCurrentSemesterSpecified: Boolean,
    val specifiedCurrentSemester: Pair<Int, SemesterType>? = null,
)
