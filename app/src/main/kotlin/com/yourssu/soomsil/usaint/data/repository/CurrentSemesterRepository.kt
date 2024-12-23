package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.data.source.local.entity.toLectureVO
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import com.yourssu.soomsil.usaint.data.type.SemesterType
import com.yourssu.soomsil.usaint.data.type.toRsaintSemesterType
import com.yourssu.soomsil.usaint.ui.entities.Grade
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import dev.eatsteak.rusaint.ffi.USaintSession
import java.time.DateTimeException
import java.time.LocalDate
import javax.inject.Inject

class CurrentSemesterRepository @Inject constructor(
    private val semesterDao: SemesterDao,
    private val rusaintApi: RusaintApi,
) {
    suspend fun getLocalCurrentSemester(): Result<SemesterVO> {
        return kotlin.runCatching {
            val semester = getCurrentSemesterType()
            semesterDao.getSemesterByYearAndSemester(semester.year, semester.storeFormat)
                ?: throw Exception("current semester not found: $semester")
        }
    }

    suspend fun getRemoteCurrentSemesterLectures(
        session: USaintSession,
        semesterId: Int = -1
    ): Result<List<LectureVO>> {
        val currentSemester = getCurrentSemesterType()
        val currentClassGradeList = rusaintApi.getClassGradeList(
            session,
            currentSemester.year.toUInt(),
            currentSemester.toRsaintSemesterType()
        ).getOrElse { e ->
            return Result.failure(e)
        }
        return Result.success(currentClassGradeList.map { it.toLectureVO(semesterId) })
    }

    suspend fun getRemoteCurrentSemester(session: USaintSession): Result<SemesterVO?> {
        val currentSemester = getCurrentSemesterType()
        val currentLectures = getRemoteCurrentSemesterLectures(session).getOrElse { e ->
            return Result.failure(e)
        }
        if (currentLectures.isEmpty())
            return Result.success(null)

        val validClassGradeList = currentLectures.filter { it.grade.toGrade() != Grade.Zero }
        val gpa = validClassGradeList.sumOf { it.grade.toGrade().value.toDouble() * it.credit }
            .div(validClassGradeList.sumOf { it.credit.toDouble() }).toFloat()

        return Result.success(
            SemesterVO(
                year = currentSemester.year,
                semester = currentSemester.storeFormat,
                semesterRank = -1,
                semesterStudentCount = -1,
                overallRank = -1,
                overallStudentCount = -1,
                earnedCredit = currentLectures.sumOf { it.credit.toDouble() }.toFloat(),
                gpa = gpa,
            )
        )
    }

    fun getCurrentSemesterType(): SemesterType {
        val date = LocalDate.now()
        val month = date.monthValue
        val year = date.year

        return when (month) {
            in 3..6 -> SemesterType.One(year)       // 1학기: 3 ~ 6월
            in 7..8 -> SemesterType.Summer(year)    // 여름학기: 7 ~ 8월
            in 9..12 -> SemesterType.Two(year)      // 2학기: 9 ~ 12월
            in 1..2 -> SemesterType.Winter(year - 1)    // 겨울학기: 1~2월
            else -> throw DateTimeException("not available month: $month")
        }
    }
}