package com.yourssu.soomsil.usaint.data.model

data class StudentInfoDto(
    val name: String,       // 이름
    val department: String, // 학과/학부
    val major: String?,     // 전공
    val grade: UInt,         // 학년
    val term: UInt,          // 학기
)