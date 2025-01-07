package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import com.yourssu.soomsil.usaint.domain.type.SemesterType
import com.yourssu.soomsil.usaint.domain.type.makeSemesterType
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

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

    suspend fun getLocalSemester(semesterType: SemesterType): Result<SemesterVO> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                semesterDao.getSemesterByYearAndSemester(
                    semesterType.year,
                    semesterType.storeFormat
                ) ?: throw Exception("semester $semesterType not found")
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
            )
        })
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
}