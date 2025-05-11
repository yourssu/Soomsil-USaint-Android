package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.yourssu.soomsil.usaint.core.model.ChapelSimpleData
import com.yourssu.soomsil.usaint.core.types.SemesterType

@Entity(
    tableName = "Chapel",
    primaryKeys = ["year", "semester", "division"],
    foreignKeys = [ForeignKey(
        entity = SemesterEntity::class,
        parentColumns = ["year", "semester"],
        childColumns = ["year", "semester"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [
        Index(value = ["year", "semester"], unique = true),
        Index(value = ["division"], unique = true)
    ]
)
data class ChapelEntity(
    @ColumnInfo(defaultValue = "0")
    val year: Int,              // foreign key
    @ColumnInfo(defaultValue = "One")
    val semester: String,       // foreign key
    @ColumnInfo(defaultValue = "0")
    val division: Int,          // primary key
    val chapelTime: String,
    val chapelRoom: String,
    val floorLevel: Int,
    val seatNumber: String,
    val absenceTime: Int,
    val result: String,
    val totalAttendance: Int,
    val currentAttendance: Int,
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
    result = result,
    totalAttendance = totalAttendance,
    currentAttendance = currentAttendance,

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
    totalAttendance = totalAttendance,
    currentAttendance = currentAttendance,
    result = result,
)
