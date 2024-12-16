package com.yourssu.soomsil.usaint.ui.component.entities

import androidx.compose.runtime.Immutable

@Immutable
data class Course(
    val tier: Tier = Tier.Unknown,
    val name: String = "",
    val credit: Credit = Credit.ZERO,
    val professorName: String = "",
)

fun List<Course>.sortByTier(): List<Course> {
    return this.sortedBy {
        it.tier.toGrade().value
    }.reversed()
}
