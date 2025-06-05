package com.yourssu.soomsil.usaint.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.LectureGrade
import com.yourssu.soomsil.usaint.core.model.LectureScore
import com.yourssu.soomsil.usaint.core.types.SemesterType

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
        Index(value = ["year", "semester", "code"], unique = true),
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
    val detail: Map<String, String>? = null, // 성적 상세 정보
) {
    // TODO delete
    fun equalsIgnoreIds(other: LectureEntity): Boolean {
        return title == other.title && code == other.code && credit == other.credit &&
                grade == other.grade && score == other.score && professorName == other.professorName
    }
}

fun LectureEntity.asExternalModel() = LectureData(
    year = year,
    semester = enumValueOf<SemesterType>(semester),
    code = code,
    title = title,
    credit = credit,
    lectureGrade = LectureGrade.from(grade),
    lectureScore = LectureScore.from(score),
    professor = professorName,
    detail = detail ?: emptyMap(),
)

fun LectureData.asEntity() = LectureEntity(
    year = year,
    semester = semester.name,
    title = title,
    code = code,
    credit = credit,
    grade = lectureGrade.toString(),
    score = lectureScore.toString(),
    professorName = professor,
    detail = detail,
)
