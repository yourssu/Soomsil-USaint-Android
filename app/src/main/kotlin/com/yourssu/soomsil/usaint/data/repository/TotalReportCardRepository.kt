package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.dao.TotalReportCardDao
import com.yourssu.soomsil.usaint.data.source.local.entity.Lecture
import com.yourssu.soomsil.usaint.data.source.local.entity.Semester
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCard
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCardWithSemesters

class ReportCardRepository(
    private val totalReportCardDao: TotalReportCardDao,
    private val semesterDao: SemesterDao,
    private val lectureDao: LectureDao
) {
    fun insertTotalReportCard(totalReportCard: TotalReportCard): Int {
        return totalReportCardDao.insertTotalReportCard(totalReportCard).toInt()
    }

    fun insertSemesters(vararg semesters: Semester): List<Int> {
        return semesterDao.insertSemesters(*semesters).map { it.toInt() }
    }

    fun insertLectures(vararg lectures: Lecture): List<Int> {
        return lectureDao.insertLectures(*lectures).map { it.toInt() }
    }

    fun getTotalReportCardWithAllData(id: Int): TotalReportCardWithSemesters? {
        return totalReportCardDao.getTotalReportCardWithSemesters(id)
    }
}