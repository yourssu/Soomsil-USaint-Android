package com.yourssu.soomsil.usaint.domain.usecase

import com.yourssu.soomsil.usaint.core.types.SemesterType
import java.time.LocalDate
import javax.inject.Inject

class GetCurrentSemesterUseCase @Inject constructor() {
    operator fun invoke(): Pair<Int, SemesterType>? {
        val now = LocalDate.now()
        val year = now.year

        // 2025년도 학기 개강 ~ 종강
        // https://ssu.ac.kr/%ED%95%99%EC%82%AC/%ED%95%99%EC%82%AC%EC%9D%BC%EC%A0%95/
        // 1학기: 3/4 ~ 6/23
        // 여름학기: 6/24 ~ 7/14
        // 2학기: 9/1 ~ 12/20
        // 겨울학기: 12/22 ~ 1/15
        return when (now) {
            in LocalDate.of(year, 3, 4)..LocalDate.of(year, 6, 23) ->
                Pair(year, SemesterType.One)

            in LocalDate.of(year, 6, 24)..LocalDate.of(year, 7, 14) ->
                Pair(year, SemesterType.Summer)

            in LocalDate.of(year, 9, 1)..LocalDate.of(year, 12, 20) ->
                Pair(year, SemesterType.Two)

            in LocalDate.of(year, 12, 22)..LocalDate.of(year, 12, 31) ->
                Pair(year, SemesterType.Winter)

            in LocalDate.of(year, 1, 1)..LocalDate.of(year, 1, 15) ->
                Pair(year - 1, SemesterType.Winter)

            else -> null
        }
    }
}