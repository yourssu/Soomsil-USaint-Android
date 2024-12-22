package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.dao.TotalReportCardDao
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCardVO
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TotalReportCardRepository @Inject constructor(
    private val totalReportCardDao: TotalReportCardDao,
    private val rusaintApi: RusaintApi,
) {
    suspend fun getLocalReportCard(): Result<TotalReportCardVO> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                totalReportCardDao.getTotalReportCard()
                    ?: throw Exception("total report card not found")
            }
        }
    }

    suspend fun storeReportCard(totalReportCard: TotalReportCardVO): Result<Unit> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                totalReportCardDao.insertTotalReportCard(totalReportCard)
            }
        }
    }

    suspend fun deleteTotalReportCard(): Result<Unit> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) { totalReportCardDao.deleteAll() }
        }
    }

    suspend fun getRemoteReportCard(session: USaintSession): Result<TotalReportCardVO> {
        val gradeSummary = rusaintApi.getCertificatedGradeSummary(session).getOrElse { e ->
            return Result.failure(e)
        }
        val graduationStudentInfo = rusaintApi.getGraduationStudentInfo(session).getOrElse { e ->
            return Result.failure(e)
        }

        return Result.success(
            TotalReportCardVO(
                earnedCredit = gradeSummary.earnedCredits,
                graduateCredit = graduationStudentInfo.graduationPoints,
                gpa = gradeSummary.gradePointsAvarage,
            )
        )
    }

    // Stale-While-Revalidate
    fun getReportCard(session: USaintSession?): Flow<Result<TotalReportCardVO>> = flow {
        // Try loading/emit local data first
        val localResult = getLocalReportCard()
        localResult.onSuccess { localData ->
            emit(Result.success(localData))
        }

        if (session == null) return@flow

        // Attempt to get form remote data
        val remoteResult = getRemoteReportCard(session)
        remoteResult.fold(
            onSuccess = { remoteData ->
                // Emit remote data
                emit(Result.success(remoteData))
                storeReportCard(remoteData)
            },
            onFailure = { e ->
                // If no local data was previously emitted, emit the failure
                emit(Result.failure(e))
            }
        )
    }
}