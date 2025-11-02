package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.yourssu.soomsil.usaint.core.model.ChapelAttendanceData
import com.yourssu.soomsil.usaint.core.types.SemesterType


@Entity(
    tableName = "ChapelAttendance",
    primaryKeys = ["year", "semester", "classDate"],
    foreignKeys = [ForeignKey(
        entity = ChapelEntity::class,
        parentColumns = ["year", "semester", "division"],
        childColumns = ["year", "semester", "division"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [
        Index(value = ["year", "semester", "classDate"], unique = true)
    ]
)
data class ChapelAttendanceEntity(
    @ColumnInfo(defaultValue = "0")
    val year: Int,
    @ColumnInfo(defaultValue = "One")
    val semester: String,
    @ColumnInfo(defaultValue = "0")
    val division: Long,
    val classDate: String,
    val category: String,
    val instructor: String,
    val instructorDepartment: String,
    val title: String,
    val attendance: String,
    val result: String,
    val note: String
)

fun ChapelAttendanceEntity.asExternalModel() = ChapelAttendanceData(
    year = year,
    semester = SemesterType.valueOf(semester),
    division = division,
    classDate = classDate,
    category = category,
    instructor = instructor,
    instructorDepartment = instructorDepartment,
    title = title,
    attendance = attendance,
    result = result,
    note = note
)

fun ChapelAttendanceData.asEntity() = ChapelAttendanceEntity(
    year = year,
    semester = semester.name,
    division = division,
    classDate = classDate,
    category = category,
    instructor = instructor,
    instructorDepartment = instructorDepartment,
    title = title,
    attendance = attendance,
    result = result,
    note = note
)
