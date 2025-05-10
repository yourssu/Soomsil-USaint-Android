package com.yourssu.soomsil.usaint.core.model

import com.yourssu.soomsil.usaint.core.types.SemesterType

data class ChapelData(
    val year: Int,
    val semester: SemesterType,
    val division: Int,
    val chapelTime: String,
    val chapelRoom: String,
    val floorLevel: Int,
    val seatNumber: String,
    val absenceTime: Int,
    val result: String
) {
    companion object {
        val previewData = ChapelData(
            2025,
            SemesterType.One,
            division = 100000001,
            chapelTime = "(수) 13:30~14:20",
            chapelRoom = "대강당",
            floorLevel = 1,
            seatNumber = "H-5-9",
            absenceTime = 3,
            result = "이게모여",
        )
    }
}