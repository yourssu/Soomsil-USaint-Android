package com.yourssu.soomsil.usaint.domain.type

import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO

// Pair<before, after>
data class LectureDiff(
    val title: String,
    val code: String,
    val credit: Pair<Float, Float>?,
    val grade: Pair<String, String>?,
    val score: Pair<String, String>?,
)

fun LectureVO.diff(other: LectureVO) = LectureDiff(
    title = title,
    code = code,
    credit = if (credit == other.credit) null else (credit to other.credit),
    grade = if (grade == other.grade) null else (grade to other.grade),
    score = if (score == other.score) null else (score to other.score),
)