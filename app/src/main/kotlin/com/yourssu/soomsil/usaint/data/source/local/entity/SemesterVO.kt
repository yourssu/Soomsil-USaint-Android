package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Semester",
    foreignKeys = [ForeignKey(
        entity = TotalReportCardVO::class,
        parentColumns = ["id"],
        childColumns = ["totalReportCardId"],
    )],
    indices = [
        Index(value = ["totalReportCardId"]),
        Index(value = ["year", "semester"], unique = true) // (year, semester) 쌍에 유니크 인덱스
    ],
)
data class SemesterVO(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val year: Int,                  // ex: 2024
    val semester: String,           // ex: “1”, “여름”
    val semesterRank: Int,          // 학기 석차
    val semesterStudentCount: Int,  // 학기 수강생 수
    val overallRank: Int,           // 전체 석차
    val overallStudentCount: Int,   // 전체 수강생 수
    val earnedCredit: Float,        // 학기 취득 학점
    val gpa: Float,                 // 학기 평균 학점
    @ColumnInfo("totalReportCardId")
    val totalReportCardId: Int = 1, // foreign key
)
