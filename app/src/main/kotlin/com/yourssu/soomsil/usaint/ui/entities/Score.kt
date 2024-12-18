package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable

@Immutable
@JvmInline
value class Score(val value: Float) : Comparable<Score> {
    init {
        require(value >= 0) {
            "학점은 음수가 될 수 없습니다. (value=$value)"
        }
    }

    fun formatToString(digit: Int = 2): String {
        return when (digit) {
            1 -> String.format("%.1f", value)
            2 -> String.format("%.2f", value)
            else -> value.toString()
        }
    }

    operator fun plus(other: Score) = Score(value + other.value)

    operator fun minus(other: Score) = Score(value - other.value)

    override fun compareTo(other: Score): Int {
        return value.compareTo(other.value)
    }

    companion object {
        val Max = Score(4.5f)
        val Zero = Score(0.0f)
    }
}

fun Float.toGrade(): Score = Score(this)

fun Double.toGrade(): Score = Score(this.toFloat())

fun Int.toGrade(): Score = Score(this.toFloat())

fun String.toGrade(): Score = when (this) {
    "A+" -> 4.5.toGrade()
    "A0", "A" -> 4.3.toGrade()
    "A-" -> 4.0.toGrade()
    "B+" -> 3.5.toGrade()
    "B0", "B" -> 3.3.toGrade()
    "B-" -> 3.0.toGrade()
    "C+" -> 2.5.toGrade()
    "C0", "C" -> 2.3.toGrade()
    "C-" -> 2.0.toGrade()
    "D+" -> 1.5.toGrade()
    "D0", "D" -> 1.3.toGrade()
    "D-" -> 1.0.toGrade()
    else -> Score.Zero
}
