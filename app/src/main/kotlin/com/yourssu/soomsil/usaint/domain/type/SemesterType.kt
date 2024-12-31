package com.yourssu.soomsil.usaint.domain.type

typealias RusaintSemesterType = dev.eatsteak.rusaint.core.SemesterType

sealed class SemesterType(
    val storeFormat: String,
    val isSeasonal: Boolean,
    private val order: Int,
) : Comparable<SemesterType> {

    abstract val year: Int

    val fullName: String
        get() = "${year}년 ${storeFormat}학기"

    val shortHandedName: String
        get() = "${year % 100}-${storeFormat}"

    data class One(override val year: Int) : SemesterType("1", false, order = 0)
    data class Summer(override val year: Int) : SemesterType("여름", true, order = 1)
    data class Two(override val year: Int) : SemesterType("2", false, order = 2)
    data class Winter(override val year: Int) : SemesterType("겨울", true, order = 3)

    override fun compareTo(other: SemesterType): Int {
        if (this == other) return 0
        if (year == other.year)
            return order.compareTo(other.order)
        return year.compareTo(other.year)
    }
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

fun SemesterType.toRusaintSemesterType(): RusaintSemesterType {
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
