package com.yourssu.soomsil.usaint.data.source.remote.rusaint

import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import dev.eatsteak.rusaint.core.GradeSummary
import dev.eatsteak.rusaint.core.GraduationStudent

internal fun GraduationStudent.asExternalModel() = StudentData(
    id = number.toString(),
    name = name,
    grade = grade.toInt(),
    semester = semester.toInt(),
    status = status,
    applyYear = applyYear.toInt(),
    applyType = applyType,
    department = department,
    majors = majors,
)

internal fun GradeSummary.asExternalModel(
    graduationPoints: Float,
    completedPoints: Float,
) = ReportCardSummaryData(
    attemptedCredits = attemptedCredits,
    earnedCredits = earnedCredits,
    gradePointsSum = gradePointsSum,
    gradePointsAverage = gradePointsAvarage,
    arithmeticMean = arithmeticMean,
    pfEarnedCredits = pfEarnedCredits,
    graduationPoints = graduationPoints,
    completedPoints = completedPoints,
)

typealias RusaintSemesterType = dev.eatsteak.rusaint.core.SemesterType

@Suppress("unused")
internal fun String.toRusaintSemesterType(): RusaintSemesterType = when {
    contains("1") -> RusaintSemesterType.ONE
    contains("여름") -> RusaintSemesterType.SUMMER
    contains("2") -> RusaintSemesterType.TWO
    contains("겨울") -> RusaintSemesterType.WINTER
    else -> throw Exception("undefined semester string: $this")
}

internal fun SemesterType.toRusaintSemesterType(): RusaintSemesterType = when (this) {
    SemesterType.One -> RusaintSemesterType.ONE
    SemesterType.Summer -> RusaintSemesterType.SUMMER
    SemesterType.Two -> RusaintSemesterType.TWO
    SemesterType.Winter -> RusaintSemesterType.WINTER
}
