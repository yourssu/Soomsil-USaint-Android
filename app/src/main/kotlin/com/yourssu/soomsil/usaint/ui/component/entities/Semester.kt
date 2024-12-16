package com.yourssu.soomsil.usaint.ui.component.entities

import androidx.compose.runtime.Immutable

@Immutable
data class Semester(
    val axisName: String = "", // ex: 1-2
    val fullName: String = "", // ex: 2020년 2학기
    val gpa: Grade = Grade.ZERO, // 평균 학점
    val appliedCredit: Credit = Credit.ZERO, // 신청 학점
    val earnedCredit: Credit = Credit.ZERO, // 취득 학점
    val passFailCredit: Credit = Credit.ZERO, // P/F 학점
    val semesterRank: Int = 0, // 학기별 석차
    val semesterStudentCount: Int = 0, // 해당 학기의 수강생 수
    val overallRank: Int = 0, // 전체 석차
    val overallStudentCount: Int = 0, // 전체 학생 수
    val isSeasonal: Boolean = false, // 계절 학기 여부
)
