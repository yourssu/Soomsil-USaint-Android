package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.dao.TotalReportCardDao
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterWithLectures
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCardVO
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCardWithSemesters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReportCardRepository1(
    private val totalReportCardDao: TotalReportCardDao,
    private val semesterDao: SemesterDao,
    private val lectureDao: LectureDao
) {
    // TotalReportCard
    suspend fun upsertTotalReportCard(totalReportCard: TotalReportCardVO) =
        withContext(Dispatchers.IO) {
            totalReportCardDao.insertTotalReportCard(totalReportCard)
        }

    suspend fun getTotalReportCard(): TotalReportCardVO? = withContext(Dispatchers.IO) {
        totalReportCardDao.getTotalReportCard()
    }

    suspend fun getTotalReportCardWithSemesters(): TotalReportCardWithSemesters? =
        withContext(Dispatchers.IO) {
            totalReportCardDao.getTotalReportCardWithSemesters()
        }


    // Semester
    suspend fun upsertSemester(semester: SemesterVO) = withContext(Dispatchers.IO) {
        semesterDao.insertSemester(semester)
    }

    suspend fun getSemester(year: Int, semesterName: String): SemesterVO? =
        withContext(Dispatchers.IO) {
            semesterDao.getSemesterByYearAndSemester(year, semesterName)
        }

    suspend fun getSemestersByTotalReportCardId(totalReportCardId: Int): List<SemesterVO> =
        withContext(Dispatchers.IO) {
            semesterDao.getSemestersByTotalReportCardId(totalReportCardId)
        }

    suspend fun getSemesterWithLectures(year: Int, semesterName: String): SemesterWithLectures? =
        withContext(Dispatchers.IO) {
            semesterDao.getSemesterWithLectures(year, semesterName)
        }


    // Lecture 관련 로직
    suspend fun upsertLecture(lecture: LectureVO) = withContext(Dispatchers.IO) {
        lectureDao.insertLecture(lecture)
    }

    suspend fun getLectureByCode(code: String): LectureVO? = withContext(Dispatchers.IO) {
        lectureDao.getLectureByCode(code)
    }

    suspend fun getLecturesBySemesterId(semesterId: Int): List<LectureVO> =
        withContext(Dispatchers.IO) {
            lectureDao.getLecturesBySemesterId(semesterId)
        }

    // === 종합적인 활용 예시 ===
    // 예: 특정 TotalReportCard(항상 id=1)와 관련된 모든 데이터 가져오기
    // getTotalReportCardWithSemesters() 호출 후, 각 Semester에 대해 getSemesterWithLectures()로 조회 가능.
    // 하지만 이미 관계형 DTO(예: SemesterWithLectures)가 있다면, 필요한 로직에 따라
    // Repository 메서드를 추가하여 한 번에 가져오는 로직 구현도 가능.
}
