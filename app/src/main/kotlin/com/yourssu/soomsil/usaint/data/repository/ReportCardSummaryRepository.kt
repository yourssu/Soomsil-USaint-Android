package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.dao.TotalReportCardDao
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCardVO
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReportCardSummaryRepository @Inject constructor(
    private val totalReportCardDao: TotalReportCardDao,
    private val rusaintApi: RusaintApi,
    private val uSaintSessionRepository: USaintSessionRepository,
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

    suspend fun getRemoteReportCard(): Result<TotalReportCardVO> {
        val session = uSaintSessionRepository.getSession().getOrElse { e ->
            return Result.failure(e)
        }
        return getRemoteReportCard(session)
    }
}