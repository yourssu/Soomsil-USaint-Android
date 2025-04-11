package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SemesterWithLectures(
    @Embedded val semester: SemesterVO,
    @Relation(
        parentColumn = "semester",
        entityColumn = "semester"
    )
    val lectures: List<LectureVO>
)
