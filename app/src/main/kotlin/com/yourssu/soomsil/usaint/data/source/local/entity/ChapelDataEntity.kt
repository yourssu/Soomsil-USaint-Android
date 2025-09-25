package com.yourssu.soomsil.usaint.data.source.local.entity

data class ChapelDataWithAttendance(
    val chapel: ChapelEntity,
    val attendances: List<ChapelAttendanceEntity>
)
