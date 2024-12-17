package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SemesterWithLectures(
    @Embedded val semester: Semester,
    @Relation(
        parentColumn = "id",
        entityColumn = "semesterId",
    )
    val lectures: List<Lecture>
)
