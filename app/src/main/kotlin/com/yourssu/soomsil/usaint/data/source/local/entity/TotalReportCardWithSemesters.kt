package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TotalReportCardWithSemesters(
    @Embedded val totalReportCard: TotalReportCardVO,
    @Relation(
        parentColumn = "id",
        entityColumn = "totalReportCardId"
    )
    val semesters: List<SemesterVO>
)
