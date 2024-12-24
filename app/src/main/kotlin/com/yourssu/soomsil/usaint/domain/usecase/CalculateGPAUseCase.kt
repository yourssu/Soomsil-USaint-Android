package com.yourssu.soomsil.usaint.domain.usecase

import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.ui.entities.Grade
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import javax.inject.Inject

class CalculateGPAUseCase @Inject constructor() {
    operator fun invoke(lectures: List<LectureVO>): Float {
        val validLectures = lectures.filter { it.grade.toGrade() != Grade.Zero }
        val creditSum = validLectures.sumOf { it.credit.toDouble() }.toFloat()
        if (creditSum == 0.0f) return 0.0f

        return validLectures.sumOf { it.grade.toGrade().value.toDouble() * it.credit }
            .div(creditSum).toFloat()
    }
}