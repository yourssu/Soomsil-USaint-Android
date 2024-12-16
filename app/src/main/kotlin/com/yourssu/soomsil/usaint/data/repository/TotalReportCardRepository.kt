package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.dao.TotalReportCardDao
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCard

class TotalReportCardRepository(
    private val totalReportCardDao: TotalReportCardDao
) {
    suspend fun getTotalReportCard() = totalReportCardDao.getTotalReportCard()

    suspend fun insertTotalReportCard(earnedCredit: Float, gpa: Float) {
        totalReportCardDao.insertTotalReportCard(totalReportCard = TotalReportCard(earnedCredit = earnedCredit, gpa = gpa))
    }
}