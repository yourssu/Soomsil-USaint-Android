package com.yourssu.soomsil.usaint.ui.types

import androidx.compose.runtime.Immutable
import com.yourssu.soomsil.usaint.model.StudentInfoDto

@Immutable
data class StudentInfo(
    val name: String,       // 이름
    val department: String, // 학과/학부
    val grade: Int,         // 학년
)

fun StudentInfoDto.toStudentInfo() = StudentInfo(
    name = name,
    department = department,
    grade = grade.toInt(),
)
