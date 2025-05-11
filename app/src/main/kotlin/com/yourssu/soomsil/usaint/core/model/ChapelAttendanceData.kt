package com.yourssu.soomsil.usaint.core.model

data class ChapelAttendanceData(
    val division: Int,
    val classDate: String,
    val category: String,
    val instructor: String,
    val instructorDepartment: String,
    val title: String,
    val attendance: String,
    val result: String,
    val note: String
) {
    companion object {
        val previewData = ChapelAttendanceData(
            division = 100012345,
            classDate = "2024-3-29",
            category = "메세지 채플",
            instructor = "김김김",
            instructorDepartment = "숭실대",
            title = "채플",
            attendance = "출석",
            result = "보통",
            note = ""
        )
    }
}
