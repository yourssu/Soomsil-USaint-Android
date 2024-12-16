package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = TotalReportCard::class,
        parentColumns = ["id"],
        childColumns = ["totalReportCardId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["totalReportCardId"])]
)
data class Semester(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val year: Int, // ex: 2024
    val semester: String, // ex: (“1학기”, “겨울학기”)
    val semesterRank: Int, // 학기 석차
    val semesterStudentCount: Int, // 학기 수강생 수
    val overallRank: Int, // 전체 석차
    val overallStudentCount: Int, // 전체 수강생 수
    val earnedCredit: Float, // 학기 취득 학점
    val gpa: Float, // 학기 평균 학점
    val totalReportCardId: Int
)