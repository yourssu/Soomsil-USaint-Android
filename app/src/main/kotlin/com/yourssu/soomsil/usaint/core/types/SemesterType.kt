package com.yourssu.soomsil.usaint.core.types

enum class SemesterType {
    One,
    Summer,
    Two,
    Winter;

    companion object {
        fun from(str: String): SemesterType {
            return when {
                str.contains("1") -> One
                str.contains("2") -> Two
                str.contains("여름") || str.uppercase().contains("SUMMER") -> Summer
                str.contains("겨울") || str.uppercase().contains("WINTER") -> Winter
                else -> throw IllegalArgumentException("unknown semester string: $str")
            }
        }
    }
}
