package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.domain.type.SemesterType
import com.yourssu.soomsil.usaint.domain.type.makeSemesterType

@Immutable
data class Semester(
    val type: SemesterType,                     // 학기의 종류
    val gpa: Grade = Grade.Zero,                // 평균 학점
    val earnedCredit: Credit = Credit.Zero,     // 취득 학점
    val semesterRank: Int = 0,                  // 학기별 석차
    val semesterStudentCount: Int = 0,          // 해당 학기의 수강생 수
    val overallRank: Int = 0,                   // 전체 석차
    val overallStudentCount: Int = 0,           // 전체 학생 수
//    val appliedCredit: Credit = Credit.Zero,    // 신청 학점
//    val passFailCredit: Credit = Credit.Zero,   // P/F 학점
)

fun SemesterVO.toSemester() = Semester(
    type = makeSemesterType(year, semester),
    gpa = gpa.toGrade(),
    earnedCredit = earnedCredit.toCredit(),
    semesterRank = semesterRank,
    semesterStudentCount = semesterStudentCount,
    overallRank = overallRank,
    overallStudentCount = overallStudentCount,
)