package com.yourssu.soomsil.usaint.data.source.remote.rusaint

import com.yourssu.soomsil.usaint.core.model.ReportCardData
import com.yourssu.soomsil.usaint.core.model.StudentData
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
    graduationPoints = graduationPoints,
    completedPoints = completedPoints,
)

internal fun GradeSummary.asExternalModel() = ReportCardData(
    attemptedCredits = attemptedCredits,
    earnedCredits = earnedCredits,
    gradePointsSum = gradePointsSum,
    gradePointsAverage = gradePointsAvarage,
    arithmeticMean = arithmeticMean,
    pfEarnedCredits = pfEarnedCredits,
)
