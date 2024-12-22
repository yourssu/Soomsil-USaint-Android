package com.yourssu.soomsil.usaint.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import com.yourssu.soomsil.usaint.data.type.SemesterType
import com.yourssu.soomsil.usaint.data.type.makeSemesterType
import com.yourssu.soomsil.usaint.data.type.toRsaintSemesterType
import com.yourssu.soomsil.usaint.ui.entities.Grade
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt

class SemesterRepository @Inject constructor(
    private val semesterDao: SemesterDao,
    private val rusaintApi: RusaintApi,
) {
    suspend fun getAllLocalSemesters(): Result<List<SemesterVO>> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                semesterDao.getSemestersByTotalReportCardId(totalReportCardId = 1)
            }
        }
    }

    suspend fun getLocalSemester(year: Int, semesterType: SemesterType): Result<SemesterVO> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                semesterDao.getSemesterByYearAndSemester(year, semesterType.storeFormat)
                    ?: throw Exception("semester (year=$year, semester=$semesterType) not found")
            }
        }
    }

    suspend fun storeSemesters(vararg semesters: SemesterVO): Result<Unit> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                semesters.forEach { semesterDao.insertSemester(it) }
            }
        }
    }

    suspend fun deleteAllSemester(): Result<Unit> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) { semesterDao.deleteAll() }
        }
    }

    suspend fun getAllRemoteSemesters(session: USaintSession): Result<List<SemesterVO>> {
        val semesterGradeList = rusaintApi.getSemesterGradeList(session).getOrElse { e ->
            return Result.failure(e)
        }

        return Result.success(semesterGradeList.map { semesterGrade ->
            val year = semesterGrade.year.toInt()
            val semester = makeSemesterType(year, semesterGrade.semester).storeFormat
            SemesterVO(
                year = year,
                semester = semester,
                semesterRank = semesterGrade.semesterRank.first.toInt(),
                semesterStudentCount = semesterGrade.semesterRank.second.toInt(),
                overallRank = semesterGrade.generalRank.first.toInt(),
                overallStudentCount = semesterGrade.generalRank.second.toInt(),
                earnedCredit = semesterGrade.earnedCredits,
                gpa = semesterGrade.gradePointsAvarage,
                totalReportCardId = 1, // 항상 1
            )
        })
    }


    suspend fun getCurrentSemester(session: USaintSession): Result<SemesterVO?> {
        // 현재 학기 가져오기
        val currentSemesterType =
            currentGradeConfirmSemesterType() ?: return Result.success(null) // 성적 조회 기간이 아닌 경우
        Timber.d("currentSemesterType: $currentSemesterType")

        // 현재 학기 성적 조회
        val currentSemesterClassGradeList = rusaintApi.getClassGradeList(
            session,
            currentSemesterType.year.toUInt(),
            currentSemesterType.toRsaintSemesterType()
        ).getOrElse { e ->
            return Result.failure(e)
        }
        Timber.d("currentSemesterClassGradeList: $currentSemesterClassGradeList")

        // 없다면 조기 반환
        if(currentSemesterClassGradeList.isEmpty()) {
            return Result.success(null) // 성적 조회 기간이 아닌 경우
        }

        // 있다면 그 학기를 반환
        return Result.success(
            SemesterVO(
                year = currentSemesterType.year,
                semester = currentSemesterType.storeFormat,
                semesterRank = 0,
                semesterStudentCount = 0,
                overallRank = 0,
                overallStudentCount = 0,
                earnedCredit = currentSemesterClassGradeList.sumOf { it.gradePoints.toDouble() }
                    .toFloat(),
                gpa = currentSemesterClassGradeList.sumOf { it.rank.toGrade().value.toDouble() }
                    .div(currentSemesterClassGradeList.filter { it.rank.toGrade() != Grade.Zero }.size)
                    .toFloat(),
                totalReportCardId = 1
            )
        )
    }

    // Stale-While-Revalidate
    fun getSemesters(session: USaintSession?): Flow<Result<List<SemesterVO>>> = flow {
        // Try loading/emit local data first
        val localResult = getAllLocalSemesters()
        localResult.onSuccess { localData ->
            emit(Result.success(localData))
        }

        if (session == null) return@flow

        // Attempt to get form remote data
        val remoteResult = getAllRemoteSemesters(session)
        remoteResult.fold(
            onSuccess = { remoteData ->
                // Emit remote data
                emit(Result.success(remoteData))
                storeSemesters(*remoteData.toTypedArray())
            },
            onFailure = { e ->
                emit(Result.failure(e))
            }
        )
    }

    private fun currentGradeConfirmSemesterType(): SemesterType? {
        val date = LocalDate.now()
        val month = date.monthValue
        val year = date.year

        return when (month) {
            6 -> SemesterType.One(year)         // 6월 → 1학기 성적 조회 기간
            7 -> SemesterType.Summer(year)      // 7월 → 여름학기 성적 조회 기간
            12 -> SemesterType.Two(year)         // 12월 → 2학기 성적 조회 기간
            1 -> SemesterType.Winter(year - 1)  // 1월 → (직전연도) 겨울학기 성적 조회 기간
            else -> null                         // 그 외 달은 성적 조회 기간 아님
        }
    }
}