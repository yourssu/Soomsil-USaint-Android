package com.yourssu.soomsil.usaint.domain.type

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

fun SemesterType.toRusaintSemesterType(): dev.eatsteak.rusaint.core.SemesterType {
    return when (this) {
        is SemesterType.One -> dev.eatsteak.rusaint.core.SemesterType.ONE
        is SemesterType.Summer -> dev.eatsteak.rusaint.core.SemesterType.SUMMER
        is SemesterType.Two -> dev.eatsteak.rusaint.core.SemesterType.TWO
        is SemesterType.Winter -> dev.eatsteak.rusaint.core.SemesterType.WINTER
    }
}

fun dev.eatsteak.rusaint.core.SemesterType.toSemesterType(year: Int): SemesterType {
    return when (this) {
        dev.eatsteak.rusaint.core.SemesterType.ONE -> SemesterType.One(year)
        dev.eatsteak.rusaint.core.SemesterType.SUMMER -> SemesterType.Summer(year)
        dev.eatsteak.rusaint.core.SemesterType.TWO -> SemesterType.Two(year)
        dev.eatsteak.rusaint.core.SemesterType.WINTER -> SemesterType.Winter(year)
    }
}
