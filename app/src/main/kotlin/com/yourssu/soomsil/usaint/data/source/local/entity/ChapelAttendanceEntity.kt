package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.yourssu.soomsil.usaint.core.model.ChapelAttendanceData
import com.yourssu.soomsil.usaint.core.types.SemesterType


@Entity(
    tableName = "ChapelAttendance",
    primaryKeys = ["division", "classDate"],
    foreignKeys = [ForeignKey(
        entity = ChapelEntity::class,
        parentColumns = ["division"],
        childColumns = ["division"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [
        Index(value = ["division", "classDate"], unique = true)
    ]
)
data class ChapelAttendanceEntity(
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
    division = division / 100000,
    classDate = classDate,
    category = category,
    instructor = instructor,
    instructorDepartment = instructorDepartment,
    title = title,
    attendance = attendance,
    result = result,
    note = note
)

fun ChapelAttendanceData.asEntity(year: Int, semester: SemesterType) = ChapelAttendanceEntity(
    division = (division * 100000) + (year * 10) + semester.ordinal,
    classDate = classDate,
    category = category,
    instructor = instructor,
    instructorDepartment = instructorDepartment,
    title = title,
    attendance = attendance,
    result = result,
    note = note
)
