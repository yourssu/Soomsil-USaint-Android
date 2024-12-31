package com.yourssu.soomsil.usaint.domain.usecase

import com.yourssu.soomsil.usaint.domain.type.SemesterType
import java.time.LocalDate
import javax.inject.Inject

class GetCurrentSemesterTypeUseCase @Inject constructor() {
    operator fun invoke(): SemesterType? {
        val now = LocalDate.now()
        val year = now.year

        // X년도 성적 처리 기간
        // 1학기: X년도 6/8 ~ X년도 7/7
        // 여름학기: X년도 7/11 ~ X년도 7/25
        // 2학기: X년도 12/8 ~ X+1년도 1/7
        // 겨울학기: X+1년도 1/11 ~ X+1년도 1/26
        return when (now) {
            in LocalDate.of(year, 6, 8)..LocalDate.of(year, 7, 7) -> SemesterType.One(year)
            in LocalDate.of(year, 7, 11)..LocalDate.of(year, 7, 25) -> SemesterType.Summer(year)
            in LocalDate.of(year, 12, 8)..LocalDate.of(year, 12, 31) -> SemesterType.Two(year)
            in LocalDate.of(year, 1, 1)..LocalDate.of(year, 1, 7) -> SemesterType.Two(year - 1)
            in LocalDate.of(year, 1, 11)..LocalDate.of(year, 1, 26) -> SemesterType.Winter(year - 1)
            else -> null
        }
    }
}