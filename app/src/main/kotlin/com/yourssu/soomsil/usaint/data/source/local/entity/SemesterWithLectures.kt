package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SemesterWithLectures(
    @Embedded val semester: SemesterEntity,
    @Relation(
        parentColumn = "semester",
        entityColumn = "semester"
    )
    val lectures: List<LectureEntity>
)
