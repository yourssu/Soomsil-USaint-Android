package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Semester::class,
        parentColumns = ["id"],
        childColumns = ["semesterId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["semesterId"])]
)
data class Lecture(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String, // 과목 이름
    val code: String, // 과목 코드
    val credit: Float, // 신청 학점
    val grade: String, // 등급 : 예: A+, P, F
    val score: String, // 성적 예: "90", "Pass", "Fail"
    val professorName: String, // 교수 이름
    val semesterId: Int
)