package com.yourssu.soomsil.usaint.core.model

data class StudentData(
    val id: String,             // 학번
    val name: String,           // 이름
    val grade: Int,             // 학년
    val semester: Int,          // 이수학기 (ex: 7)
    val status: String,         // 학적 상태 (재학 or 휴학)
    val applyYear: Int,         // 입학년도
    val applyType: String,      // 입학유형 (ex: 신입학)
    val department: String,     // 소속 (ex: IT대학)
    val majors: List<String>,   // 제1전공 ~ 제4전공
    val graduationPoints: Float,// 졸업학점
    val completedPoints: Float, // 인정학점
)
