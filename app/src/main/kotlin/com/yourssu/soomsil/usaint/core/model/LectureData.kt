package com.yourssu.soomsil.usaint.core.model

import com.yourssu.soomsil.usaint.core.types.SemesterType

/**
 * 강의 정보 및 성적 정보
 */
data class LectureData(
    val year: Int,
    val semester: SemesterType,
    val code: String,
    val title: String,
    val credit: Float,
    val rank: LectureRank,
    val score: LectureScore,
    val professor: String,
    val detail: Map<String, Float> = emptyMap(),
)

sealed interface LectureRank {
    @JvmInline
    value class Rank(val rank: String) : LectureRank {
        companion object {
            val a = Rank("A+")
        }
    }
}

sealed interface LectureScore {
    @JvmInline
    value class Score(val score: Int) : LectureScore
}

data object Pass : LectureRank, LectureScore
data object Fail : LectureRank, LectureScore
