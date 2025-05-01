package com.yourssu.soomsil.usaint.domain.usecase

import com.yourssu.soomsil.usaint.core.types.GradePoint
import com.yourssu.soomsil.usaint.core.types.toGrade
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity
import javax.inject.Inject

class CalculateGPAUseCase @Inject constructor() {
    operator fun invoke(lectures: List<LectureEntity>): Float {
        val validLectures = lectures.filter { it.grade.toGrade() != GradePoint.Zero }
        val creditSum = validLectures.sumOf { it.credit.toDouble() }.toFloat()
        if (creditSum == 0.0f) return 0.0f

        return validLectures.sumOf { it.grade.toGrade().point.toDouble() * it.credit }
            .div(creditSum).toFloat()
    }
}