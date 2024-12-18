package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable

@Immutable
data class LectureInfo(
    val tier: Tier = Tier.Unknown,
    val name: String = "",
    val credit: Credit = Credit.Zero,
    val professorName: String = "",
)

fun List<LectureInfo>.sortByGrade(): List<LectureInfo> {
    return this.sortedBy {
        it.tier.toGrade().value
    }.reversed()
}
