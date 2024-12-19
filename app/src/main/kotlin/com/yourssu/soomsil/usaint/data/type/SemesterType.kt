package com.yourssu.soomsil.usaint.data.type

typealias RusaintSemesterType = dev.eatsteak.rusaint.core.SemesterType

sealed class SemesterType(val year: Int) {
    class One(year: Int) : SemesterType(year)
    class Summer(year: Int) : SemesterType(year)
    class Two(year: Int) : SemesterType(year)
    class Winter(year: Int) : SemesterType(year)
}

fun makeSemesterType(year: Int, semester: String): SemesterType {
    return when (semester) {
        "1" -> SemesterType.One(year)
        "summer" -> SemesterType.Summer(year)
        "2" -> SemesterType.Two(year)
        "winter" -> SemesterType.Winter(year)
        else -> throw Exception("unknown Semester : $semester")
    }
}

fun SemesterType.toRsaintSemesterType(): RusaintSemesterType {
    return when (this) {
        is SemesterType.One -> RusaintSemesterType.ONE
        is SemesterType.Summer -> RusaintSemesterType.SUMMER
        is SemesterType.Two -> RusaintSemesterType.TWO
        is SemesterType.Winter -> RusaintSemesterType.WINTER
    }
}

fun RusaintSemesterType.toSemesterType(year: Int): SemesterType {
    return when (this) {
        RusaintSemesterType.ONE -> SemesterType.One(year)
        RusaintSemesterType.SUMMER -> SemesterType.Summer(year)
        RusaintSemesterType.TWO -> SemesterType.Two(year)
        RusaintSemesterType.WINTER -> SemesterType.Winter(year)
    }
}
