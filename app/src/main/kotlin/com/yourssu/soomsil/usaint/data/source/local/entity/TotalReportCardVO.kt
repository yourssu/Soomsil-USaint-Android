package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "total_report_card")
data class TotalReportCardVO(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,            // 고정된 Primary Key를 사용해 항상 하나의 레코드만 유지
    val earnedCredit: Float,    // 취득 학점
    val graduateCredit: Float,  // 졸업 학점
    val gpa: Float,             // 평균 학점
)
