package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "total_report_card")
data class TotalReportCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val earnedCredit: Float, // 취득 학점
    val gpa: Float, // 평균 학점
)