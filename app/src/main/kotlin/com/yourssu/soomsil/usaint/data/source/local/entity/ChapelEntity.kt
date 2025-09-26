package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.yourssu.soomsil.usaint.core.model.ChapelSimpleData
import com.yourssu.soomsil.usaint.core.types.SemesterType

@Entity(
    tableName = "Chapel",
    primaryKeys = ["year", "semester", "division"],
    indices = [
        Index(value = ["year", "semester", "division"], unique = true)
    ]
)
data class ChapelEntity(
    @ColumnInfo(defaultValue = "0")
    val year: Int,
    @ColumnInfo(defaultValue = "One")
    val semester: String,
    @ColumnInfo(defaultValue = "0")
    val division: Long,          // primary key
    val chapelTime: String,
    val chapelRoom: String,
    val floorLevel: Int,
    val seatNumber: String,
    val absenceTime: Int,
    val result: String
)

fun ChapelEntity.asExternalModel() = ChapelSimpleData(
    year = year,
    semester = enumValueOf<SemesterType>(semester),
    division = division,
    chapelTime = chapelTime,
    chapelRoom = chapelRoom,
    floorLevel = floorLevel,
    seatNumber = seatNumber,
    absenceTime = absenceTime,
    result = result

)

fun ChapelSimpleData.asEntity() = ChapelEntity(
    year = year,
    semester = semester.name,
    division = division,
    chapelTime = chapelTime,
    chapelRoom = chapelRoom,
    floorLevel = floorLevel,
    seatNumber = seatNumber,
    absenceTime = absenceTime,
    result = result,
)
