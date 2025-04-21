package com.yourssu.soomsil.usaint.data.source.remote.rusaint

typealias RusaintSemesterType = dev.eatsteak.rusaint.core.SemesterType

fun String.toRusaintSemesterType(): RusaintSemesterType = when {
    contains("1") -> RusaintSemesterType.ONE
    contains("여름") -> RusaintSemesterType.SUMMER
    contains("2") -> RusaintSemesterType.TWO
    contains("겨울") -> RusaintSemesterType.WINTER
    else -> throw Exception("undefined semester string: $this")
}