package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TotalReportCardWithSemesters(
    @Embedded val totalReportCard: TotalReportCardVO,
    @Relation(
        parentColumn = "id",
        entityColumn = "totalReportCardId",
        entity = SemesterVO::class
    )
    val semesters: List<SemesterWithLectures>
)
