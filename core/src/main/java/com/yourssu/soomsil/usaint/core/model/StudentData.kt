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
) {
    companion object {
        val previewData = StudentData(
            id = "20221234",
            name = "홍길동",
            grade = 4,
            semester = 7,
            status = "재학",
            applyYear = 2022,
            applyType = "신입학",
            department = "IT대학",
            majors = listOf("컴퓨터학부"),
        )
    }
}
