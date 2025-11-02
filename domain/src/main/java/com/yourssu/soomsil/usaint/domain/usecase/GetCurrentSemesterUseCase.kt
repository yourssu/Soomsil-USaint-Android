package com.yourssu.soomsil.usaint.domain.usecase

import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.source.local.datastore.UserPreferencesDataSource
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class GetCurrentSemesterUseCase @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
) {
    suspend operator fun invoke(): Pair<Int, SemesterType>? {
        val userData = userPreferencesDataSource.userData.first()
        if (userData.isCurrentSemesterSpecified) return userData.specifiedCurrentSemester
        return default()
    }

    fun default(): Pair<Int, SemesterType>? {
        val now = LocalDate.now()
        val year = now.year

        // 2025년도 학기 개강 ~ 종강
        // https://ssu.ac.kr/%ED%95%99%EC%82%AC/%ED%95%99%EC%82%AC%EC%9D%BC%EC%A0%95/
        // 1학기: 3/4 ~ 6/23 (성적 처리기간 ~7.7)
        // 여름학기: 6/24 ~ 7/14 (성적 처리기간 ~7.31)
        // 2학기: 9/1 ~ 12/20 (성적 처리기간 ~1.7)
        // 겨울학기: 12/22 ~ 1/15 (성적 처리기간 ~1.31)
        return when (now) {
            in LocalDate.of(year, 3, 4)..LocalDate.of(year, 7, 7) ->
                Pair(year, SemesterType.One)

            in LocalDate.of(year, 7, 8)..LocalDate.of(year, 7, 14) ->
                Pair(year, SemesterType.Summer)

            in LocalDate.of(year, 9, 1)..LocalDate.of(year, 12, 31) ->
                Pair(year, SemesterType.Two)

            in LocalDate.of(year, 1, 1)..LocalDate.of(year, 1, 7) ->
                Pair(year-1, SemesterType.Two)

            in LocalDate.of(year, 1, 8)..LocalDate.of(year, 1, 31) ->
                Pair(year-1, SemesterType.Winter)

            else -> null
        }
    }
}