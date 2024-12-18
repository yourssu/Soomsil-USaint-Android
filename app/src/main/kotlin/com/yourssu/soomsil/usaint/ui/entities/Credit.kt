package com.yourssu.soomsil.usaint.ui.entities

import androidx.compose.runtime.Immutable
import java.text.DecimalFormat

@Immutable
@JvmInline
value class Credit(val value: Float) {
    fun formatToString(): String {
        return DecimalFormat("0.##").format(value) // 하위 소수점 0 버림
    }

    operator fun plus(other: Credit) = Credit(value + other.value)

    operator fun minus(other: Credit) = Credit(value - other.value)

    companion object {
        val ZERO = Credit(0.0f)
    }
}

fun Float.toCredit() = Credit(this)

fun Int.toCredit(): Credit = Credit(this.toFloat())

fun Double.toCredit(): Credit = Credit(this.toFloat())
