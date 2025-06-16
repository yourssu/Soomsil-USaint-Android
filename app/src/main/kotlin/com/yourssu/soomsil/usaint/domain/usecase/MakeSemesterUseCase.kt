package com.yourssu.soomsil.usaint.domain.usecase

import com.yourssu.soomsil.usaint.core.model.Fail
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.LectureScore
import com.yourssu.soomsil.usaint.core.model.Pass
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.model.Unknown
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.core.types.toGrade
import javax.inject.Inject

class MakeSemesterUseCase @Inject constructor() {
    operator fun invoke(
        currentSemester: Pair<Int, SemesterType>,
        lectures: List<LectureData>
    ): SemesterData {

        val gpaLectures = lectures
            .filter { it.lectureScore is LectureScore.Score && it.lectureGrade !is Pass }
        val gradePointsSum = gpaLectures.sumOf {
            it.lectureGrade.toString().toGrade().point * it.credit.toDouble()
        }
        val gpaCredits = gpaLectures.sumOf { it.credit.toDouble() }
        val gradePointsAverage = if (gpaCredits > 0) {
            (gradePointsSum / gpaCredits).toFloat()
        } else {
            0f
        }

        val x = SemesterData(
            year = currentSemester.first,
            semester = currentSemester.second,
            gradePointsAverage = gradePointsAverage,
            attemptedCredit = lectures.sumOf { it.credit.toDouble() }.toFloat(),
            earnedCredit = lectures
                .filterNot { it.lectureGrade is Fail || it.lectureGrade is Unknown }
                .sumOf { it.credit.toDouble() }.toFloat(),
            pfEarnedCredit = lectures
                .filter { it.lectureGrade is Pass }
                .sumOf { it.credit.toDouble() }.toFloat(),
            semesterRank = 0 to 0,
            generalRank = 0 to 0,
        )
        return x
    }
}