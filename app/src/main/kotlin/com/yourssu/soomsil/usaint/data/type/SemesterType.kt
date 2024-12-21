package com.yourssu.soomsil.usaint.data.type

typealias RusaintSemesterType = dev.eatsteak.rusaint.core.SemesterType

sealed class SemesterType(val year: Int, val storeFormat: String) {
    class One(year: Int) : SemesterType(year, "1")
    class Summer(year: Int) : SemesterType(year, "여름")
    class Two(year: Int) : SemesterType(year, "2")
    class Winter(year: Int) : SemesterType(year, "겨울")
}

fun makeSemesterType(year: Int, semester: String): SemesterType {
    return semester.run {
        when {
            contains("1") -> SemesterType.One(year)
            contains("2") -> SemesterType.Two(year)
            contains("summer") || contains("여름") -> SemesterType.Summer(year)
            contains("winter") || contains("겨울") -> SemesterType.Winter(year)
            else -> throw Exception("unknown Semester : $semester")
        }
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
