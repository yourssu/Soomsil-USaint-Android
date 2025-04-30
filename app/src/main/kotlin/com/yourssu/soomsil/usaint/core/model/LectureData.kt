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
    val lectureGrade: LectureGrade,
    val lectureScore: LectureScore,
    val professor: String,
    val detail: Map<String, String> = emptyMap(),
)

sealed interface LectureGrade {
    @JvmInline
    value class Grade(val grade: String) : LectureGrade {
        override fun toString(): String = grade
    }

    companion object {
        fun from(str: String): LectureGrade {
            return when (str) {
                "A+", "A0", "A-", "B+", "B0", "B-",
                "C+", "C0", "C-", "D+", "D0", "D-" -> Grade(str)

                else -> if (str.uppercase().contains("P")) {
                    Pass
                } else if (str.uppercase().contains("F")) {
                    Fail
                } else {
                    Unknown
                }
            }
        }
    }
}

sealed interface LectureScore {
    @JvmInline
    value class Score(val score: Int) : LectureScore {
        override fun toString(): String = score.toString()
    }

    companion object {
        fun from(str: String): LectureScore {
            return when {
                str.uppercase().contains("P") -> Pass
                str.uppercase().contains("F") -> Fail
                else -> str.toIntOrNull()?.let { Score(it) } ?: Unknown
            }
        }
    }
}

data object Pass : LectureGrade, LectureScore {
    override fun toString(): String = "P"
}

data object Fail : LectureGrade, LectureScore {
    override fun toString(): String = "F"
}

data object Unknown : LectureGrade, LectureScore {
    override fun toString(): String = "Unknown"
}
