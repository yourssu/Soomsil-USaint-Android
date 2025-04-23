package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import dev.eatsteak.rusaint.core.ClassGrade
import dev.eatsteak.rusaint.core.ClassScore

@Entity(
    tableName = "Lecture",
    primaryKeys = ["year", "semester", "code"],
    foreignKeys = [ForeignKey(
        entity = SemesterEntity::class,
        parentColumns = ["year", "semester"],
        childColumns = ["year", "semester"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [
        Index(value = ["year", "semester", "code"], unique = true) // code 컬럼에 고유 인덱스를 추가
    ]
)
data class LectureEntity(
    @ColumnInfo(defaultValue = "0")
    val year: Int,              // foreign key
    @ColumnInfo(defaultValue = "One")
    val semester: String,       // foreign key
    val title: String,          // 과목 이름
    val code: String,           // 과목 코드 (고유)
    val credit: Float,          // 과목 학점
    val grade: String,          // 등급 (ex: "A+", "P", "F")
    val score: String,          // 성적 (ex: "90", "P", "F")
    val professorName: String,  // 교수님 성함
    val detail: Map<String, String>?, // 성적 상세 정보
) {
    fun equalsIgnoreIds(other: LectureEntity): Boolean {
        return title == other.title && code == other.code && credit == other.credit &&
                grade == other.grade && score == other.score && professorName == other.professorName
    }
}

fun ClassGrade.toLectureEntity(): LectureEntity {
    val scoreString = when (score) {
        is ClassScore.Score -> (score as ClassScore.Score).v1.toString()
        is ClassScore.Pass -> "Pass"
        is ClassScore.Failed -> "Failed"
        is ClassScore.Empty -> "Empty"
    }

    return LectureEntity(
        title = className,
        code = code,
        credit = gradePoints,
        grade = rank,
        score = scoreString,
        professorName = professor,
        year = year.toIntOrNull() ?: -1,
        semester = semester.replace("학기", ""), // "1학기"를 "1"로 만들기 위함
        detail = emptyMap(),
    )
}
