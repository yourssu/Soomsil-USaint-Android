package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO

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

fun LectureVO.toLectureInfo() = LectureInfo(
    tier = Tier(grade),
    name = title,
    credit = credit.toCredit(),
    professorName = professorName,
)