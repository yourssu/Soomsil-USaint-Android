package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import com.yourssu.soomsil.usaint.data.type.SemesterType
import com.yourssu.soomsil.usaint.data.type.toRsaintSemesterType
import dev.eatsteak.rusaint.core.ClassScore
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LectureRepository @Inject constructor(
    private val lectureDao: LectureDao,
    private val semesterDao: SemesterDao,
    private val rusaintApi: RusaintApi,
    private val semesterRepo: SemesterRepository,
) {
    suspend fun getLocalLectures(semester: SemesterType): Result<List<LectureVO>> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                semesterDao.getSemesterWithLectures(
                    year = semester.year,
                    semesterName = semester.storeFormat
                )?.lectures ?: throw Exception("semester(${semester})'s lectures not found")
            }
        }
    }

    suspend fun storeLectures(vararg lectures: LectureVO): Result<Unit> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                lectures.forEach { lectureDao.insertLecture(it) }
            }
        }
    }

    suspend fun deleteAllLectures(): Result<Unit> {
        return kotlin.runCatching {
            withContext(Dispatchers.IO) { lectureDao.deleteAll() }
        }
    }

    suspend fun getRemoteLectures(
        session: USaintSession,
        semester: SemesterType
    ): Result<List<LectureVO>> {
        val classGradeList = rusaintApi.getClassGradeList(
            session,
            semester.year.toUInt(),
            semester.toRsaintSemesterType()
        ).getOrElse { e ->
            return Result.failure(e)
        }

        val semesterId = semesterRepo.getLocalSemester(semester.year, semester).getOrElse { e ->
            return Result.failure(e)
        }.id

        return Result.success(classGradeList.map { classGrade ->
            val scoreString = when (classGrade.score) {
                is ClassScore.Score -> (classGrade.score as ClassScore.Score).v1.toString()
                is ClassScore.Pass -> "Pass"
                is ClassScore.Failed -> "Failed"
            }
            LectureVO(
                title = classGrade.className,
                code = classGrade.code,
                credit = classGrade.gradePoints,
                grade = classGrade.rank,
                score = scoreString,
                professorName = classGrade.professor,
                semesterId = semesterId,
            )
        })
    }
}