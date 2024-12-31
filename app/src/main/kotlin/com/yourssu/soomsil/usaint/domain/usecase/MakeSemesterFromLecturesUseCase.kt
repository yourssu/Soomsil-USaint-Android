package com.yourssu.soomsil.usaint.domain.usecase

import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.domain.type.SemesterType
import javax.inject.Inject

class MakeSemesterFromLecturesUseCase @Inject constructor(
    private val gpaUseCase: CalculateGPAUseCase,
) {
    // 성적 정보를 토대로 임시 SemesterVO 객체를 만들어 반환합니다.
    operator fun invoke(semesterType: SemesterType, lectures: List<LectureVO>): SemesterVO {
        return SemesterVO(
            year = semesterType.year,
            semester = semesterType.storeFormat,
            semesterRank = -1,
            semesterStudentCount = -1,
            overallRank = -1,
            overallStudentCount = -1,
            earnedCredit = lectures.sumOf { it.credit.toDouble() }.toFloat(),
            gpa = gpaUseCase(lectures)
        )
    }
}