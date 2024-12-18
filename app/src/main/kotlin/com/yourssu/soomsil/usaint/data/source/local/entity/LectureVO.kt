package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Lecture",
    foreignKeys = [ForeignKey(
        entity = SemesterVO::class,
        parentColumns = ["id"],
        childColumns = ["semesterId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["semesterId"]),
        Index(value = ["code"], unique = true) // code 컬럼에 고유 인덱스를 추가
    ]
)
data class LectureVO(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,          // 과목 이름
    val code: String,           // 과목 코드 (고유)
    val credit: Float,          // 신청 학점
    val grade: String,          // 등급 (ex: "A+", "P", "F")
    val score: String,          // 성적 (ex: "90", "Pass", "Fail")
    val professorName: String,  // 교수님 성함
    @ColumnInfo("semesterId")
    val semesterId: Int,        // foreign key
)
