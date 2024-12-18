package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable

@Immutable
data class LectureInfo(
    val grade: Grade = Grade.Unknown,
    val name: String = "",
    val credit: Credit = Credit.Zero,
    val professorName: String = "",
)

fun List<LectureInfo>.sortByGrade(): List<LectureInfo> {
    return this.sortedBy {
        it.grade.toGrade().value
    }.reversed()
}
