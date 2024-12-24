package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.eatsteak.rusaint.core.ClassGrade
import dev.eatsteak.rusaint.core.ClassScore

@Entity(
    tableName = "Lecture",
    foreignKeys = [ForeignKey(
        entity = SemesterVO::class,
        parentColumns = ["id"],
        childColumns = ["semesterId"],
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
    val credit: Float,          // 과목 학점
    val grade: String,          // 등급 (ex: "A+", "P", "F")
    val score: String,          // 성적 (ex: "90", "Pass", "Failed")
    val professorName: String,  // 교수님 성함
    @ColumnInfo("semesterId")
    val semesterId: Int,        // foreign key
)

fun ClassGrade.toLectureVO(semesterId: Int): LectureVO {
    val scoreString = when (score) {
        is ClassScore.Score -> (score as ClassScore.Score).v1.toString()
        is ClassScore.Pass -> "Pass"
        is ClassScore.Failed -> "Failed"
        is ClassScore.Empty -> "Empty"
    }
    return LectureVO(
        title = className,
        code = code,
        credit = gradePoints,
        grade = rank,
        score = scoreString,
        professorName = professor,
        semesterId = semesterId,
    )
}
