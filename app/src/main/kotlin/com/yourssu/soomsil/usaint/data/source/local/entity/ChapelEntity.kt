package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.types.SemesterType

@Entity(
    tableName = "Chapel",
    primaryKeys = ["year", "semester"],
    foreignKeys = [ForeignKey(
        entity = ChapelEntity::class,
        parentColumns = ["year", "semester"],
        childColumns = ["year", "semester"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [
        Index(value = ["year", "semester"], unique = true) // code 컬럼에 고유 인덱스를 추가
    ]
)
data class ChapelEntity(
    @ColumnInfo(defaultValue = "0")
    val year: Int,              // foreign key
    @ColumnInfo(defaultValue = "One")
    val semester: String,       // foreign key
    val division: Int,
    val chapelTime: String,
    val chapelRoom: String,
    val floorLevel: Int,
    val seatNumber: String,
    val absenceTime: Int,
    val result: String
)

fun ChapelEntity.asExternalModel() = ChapelData(
    year = year,
    semester = enumValueOf<SemesterType>(semester),
    division = division,
    chapelTime = chapelTime,
    chapelRoom = chapelRoom,
    floorLevel = floorLevel,
    seatNumber = seatNumber,
    absenceTime = absenceTime,
    result = result,
)

fun ChapelData.asEntity() = ChapelEntity(
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
