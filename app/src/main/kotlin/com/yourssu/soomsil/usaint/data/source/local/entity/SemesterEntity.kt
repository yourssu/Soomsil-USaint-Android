package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Entity
import androidx.room.Index
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.types.SemesterType

@Entity(
    tableName = "Semester",
    primaryKeys = ["year", "semester"], // 복합키
    indices = [
        Index(value = ["year", "semester"], unique = true) // (year, semester) 쌍에 유니크 인덱스
    ],
)
data class SemesterEntity(
    val year: Int,                  // ex: 2024
    val semester: String,           // ex: “1”, “여름”
    val semesterRank: Int,          // 학기 석차
    val semesterStudentCount: Int,  // 학기 수강생 수
    val overallRank: Int,           // 전체 석차
    val overallStudentCount: Int,   // 전체 수강생 수
    val earnedCredit: Float,        // 학기 취득 학점
    val gpa: Float,                 // 학기 평균 학점
)

fun SemesterEntity.asExternalModel() = SemesterData(
    year = year,
    semester = SemesterType.from(semester),
    gradePointsAverage = gpa,
    attemptedCredit = 0f, // TODO
    earnedCredit = earnedCredit,
    pfEarnedCredit = 0f, // TODO
    semesterRank = Pair(semesterRank, semesterStudentCount),
    generalRank = Pair(overallRank, overallStudentCount),
)
