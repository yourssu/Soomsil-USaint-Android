package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ChapelDataWithAttendance(
    @Embedded val chapel: ChapelEntity,
    @Relation(
        parentColumn = "division",
        entityColumn = "division"
    )
    val attendances: List<ChapelAttendanceEntity>
)
