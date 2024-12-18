package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable

@Immutable
data class StudentInfo(
    val name: String,       // 이름
    val department: String, // 학과/학부
    val grade: Int,         // 학년
)
