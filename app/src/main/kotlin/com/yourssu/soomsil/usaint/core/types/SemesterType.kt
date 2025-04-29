package com.yourssu.soomsil.usaint.core.types

enum class SemesterType(val kor: String) {
    One("1"),
    Summer("여름"),
    Two("2"),
    Winter("겨울");

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
