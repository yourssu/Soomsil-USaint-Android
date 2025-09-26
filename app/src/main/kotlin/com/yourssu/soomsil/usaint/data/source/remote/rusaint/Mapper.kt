package com.yourssu.soomsil.usaint.data.source.remote.rusaint

import com.yourssu.soomsil.usaint.core.model.ChapelAttendanceData
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.model.ChapelSimpleData
import com.yourssu.soomsil.usaint.core.model.Fail
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.LectureGrade
import com.yourssu.soomsil.usaint.core.model.LectureScore
import com.yourssu.soomsil.usaint.core.model.Pass
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.core.model.Unknown
import com.yourssu.soomsil.usaint.core.types.SemesterType
import dev.eatsteak.rusaint.core.ChapelAttendance
import dev.eatsteak.rusaint.core.ChapelInformation
import dev.eatsteak.rusaint.core.ClassGrade
import dev.eatsteak.rusaint.core.ClassScore
import dev.eatsteak.rusaint.core.GradeSummary
import dev.eatsteak.rusaint.core.GraduationStudent
import dev.eatsteak.rusaint.core.SemesterGrade

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
    gradePointsAverage = gradePointsAverage,
    arithmeticMean = arithmeticMean,
    pfEarnedCredits = pfEarnedCredits,
    graduationPoints = graduationPoints,
    completedPoints = completedPoints,
)

internal fun SemesterGrade.asExternalModel() = SemesterData(
    year = year.toInt(),
    semester = semester.toSemesterType(),
    gradePointsAverage = gradePointsAverage,
    attemptedCredit = attemptedCredits,
    earnedCredit = earnedCredits,
    pfEarnedCredit = pfEarnedCredits,
    semesterRank = Pair(semesterRank.first.toInt(), semesterRank.second.toInt()),
    generalRank = Pair(generalRank.first.toInt(), generalRank.second.toInt()),
)

internal fun ClassGrade.asExternalModel(year: Int, semester: SemesterType) = LectureData(
    year = year,
    semester = semester,
    code = code,
    title = className,
    credit = gradePoints,
    lectureGrade = LectureGrade.from(rank),
    lectureScore = score.toLectureScore(),
    professor = professor,
    detail = detail?.mapValues { (_, value) -> value.toString() } ?: emptyMap()
)

internal fun ChapelInformation.asExternalModel(year: Int, semester: SemesterType) = ChapelData(
    chapelSimpleData = ChapelSimpleData(
        year = year,
        semester = semester,
        division = generalInformation.division.toLong(),
        chapelTime = generalInformation.chapelTime,
        chapelRoom = generalInformation.chapelRoom,
        floorLevel = generalInformation.floorLevel.toInt(),
        seatNumber = generalInformation.seatNumber,
        absenceTime = generalInformation.absenceTime.toInt(),
        result = generalInformation.result,
    ),
    chapelAttendances = attendances.map {
        it.asExternalModel(year, semester)
    }

)

internal fun ChapelAttendance.asExternalModel(year: Int, semester: SemesterType) = ChapelAttendanceData(
    year = year,
    semester = semester,
    division = division.toLong(),
    classDate = classDate,
    category = category,
    instructor = instructor,
    instructorDepartment = instructorDepartment,
    title = title,
    attendance = attendance,
    result = result,
    note = note
)


private fun ClassScore.toLectureScore(): LectureScore = when (this) {
    is ClassScore.Pass -> Pass
    is ClassScore.Failed -> Fail
    is ClassScore.Score -> LectureScore.Score(v1.toInt())
    is ClassScore.Empty -> Unknown
}

internal fun String.toSemesterType(): SemesterType = when {
    contains("1") -> SemesterType.One
    contains("여름") || uppercase().contains("SUMMER") -> SemesterType.Summer
    contains("2") -> SemesterType.Two
    contains("겨울") || uppercase().contains("WINTER") -> SemesterType.Winter
    else -> throw Exception("undefined semester string: $this")
}

internal fun dev.eatsteak.rusaint.core.SemesterType.toSemesterType(): SemesterType = when (this) {
    dev.eatsteak.rusaint.core.SemesterType.ONE -> SemesterType.One
    dev.eatsteak.rusaint.core.SemesterType.SUMMER -> SemesterType.Summer
    dev.eatsteak.rusaint.core.SemesterType.TWO -> SemesterType.Two
    dev.eatsteak.rusaint.core.SemesterType.WINTER -> SemesterType.Winter
}


typealias RusaintSemesterType = dev.eatsteak.rusaint.core.SemesterType

internal fun SemesterType.toRusaintSemesterType(): RusaintSemesterType = when (this) {
    SemesterType.One -> RusaintSemesterType.ONE
    SemesterType.Summer -> RusaintSemesterType.SUMMER
    SemesterType.Two -> RusaintSemesterType.TWO
    SemesterType.Winter -> RusaintSemesterType.WINTER
}
