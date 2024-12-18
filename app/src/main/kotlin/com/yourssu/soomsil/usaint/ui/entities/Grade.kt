package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable

@Immutable
@JvmInline
value class Grade(val value: Float) : Comparable<Grade> {
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

    operator fun plus(other: Grade) = Grade(value + other.value)

    operator fun minus(other: Grade) = Grade(value - other.value)

    override fun compareTo(other: Grade): Int {
        return value.compareTo(other.value)
    }

    companion object {
        val MAX = Grade(4.5f)
        val ZERO = Grade(0.0f)
    }
}

fun Float.toGrade(): Grade = Grade(this)

fun Double.toGrade(): Grade = Grade(this.toFloat())

fun Int.toGrade(): Grade = Grade(this.toFloat())

fun String.toGrade(): Grade = when (this) {
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
    else -> Grade.ZERO
}
