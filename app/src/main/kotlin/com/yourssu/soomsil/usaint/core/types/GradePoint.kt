package com.yourssu.soomsil.usaint.core.types

import android.annotation.SuppressLint

@Suppress("unused")
@JvmInline
value class GradePoint(val point: Float) : Comparable<GradePoint> {
    init {
        require(point >= 0) {
            "학점은 음수가 될 수 없습니다. (value=$point)"
        }
    }

    @SuppressLint("DefaultLocale")
    fun formatToString(digit: Int = 2): String {
        return when (digit) {
            1 -> String.format("%.1f", point)
            2 -> String.format("%.2f", point)
            else -> point.toString()
        }
    }

    operator fun plus(other: GradePoint) = GradePoint(point + other.point)

    operator fun minus(other: GradePoint) = GradePoint(point - other.point)

    override fun compareTo(other: GradePoint): Int {
        return point.compareTo(other.point)
    }

    companion object {
        val Max = GradePoint(4.5f)
        val Zero = GradePoint(0.0f)
    }
}

fun Float.toGrade(): GradePoint = GradePoint(this)

fun Double.toGrade(): GradePoint = GradePoint(this.toFloat())

fun Int.toGrade(): GradePoint = GradePoint(this.toFloat())

fun String.toGrade(): GradePoint = when (this) {
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
    else -> GradePoint.Zero
}
