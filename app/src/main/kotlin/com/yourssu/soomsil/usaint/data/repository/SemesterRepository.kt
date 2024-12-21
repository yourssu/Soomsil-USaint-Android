package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import com.yourssu.soomsil.usaint.data.type.SemesterType
import com.yourssu.soomsil.usaint.data.type.makeSemesterType
import com.yourssu.soomsil.usaint.data.type.toRsaintSemesterType
import dev.eatsteak.rusaint.core.ClassScore
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
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


    //Result<SemesterVO>
    suspend fun getCurrentSemester(session: USaintSession): Result<SemesterVO?> {
        // 아직 성적이 다 나오지 않은 경우에는 현재 학기를 가져오려면 어떻게 해야하지?
        // 일단 로컬에 있는 학기 중 가장 최근 학기를 가져오고
        // 그 학기의 다음 학기 성적을 remote로 가져오는 방식으로 구현해보자.
        Timber.d("getCurrentSemester")

        val localSemesters = getAllLocalSemesters().getOrElse { e ->
            return Result.failure(e)
        }

        if (localSemesters.isEmpty()) {
            Timber.e("local semesters are empty")
            return Result.failure(Exception("local semesters are empty"))
        }

        // 가장 최근 년도(제일 큰)를 가져온다
        val latestSemester = localSemesters
            .maxWith(compareBy({ it.year }, { it.semester.toSemesterOrder() }))
        latestSemester.let {
            Timber.d("latest semester: ${it.year} ${it.semester}")
        }

        // 가장 최근 학기의 타입을 가져온다(변환)
        val latestSemesterType = makeSemesterType(latestSemester.year, latestSemester.semester)
        Timber.d("latest semester type: $latestSemesterType")

        // 다음 학기
        val nextSemester = when (latestSemesterType) {
            is SemesterType.One -> SemesterType.Two(latestSemesterType.year)
            is SemesterType.Summer -> SemesterType.One(latestSemesterType.year)
            is SemesterType.Two -> SemesterType.Summer(latestSemesterType.year)
            is SemesterType.Winter -> SemesterType.One(latestSemesterType.year + 1)
        }
        Timber.d("next semester: $nextSemester")

        // 다음 학기 수업 성적을 가져온다
        val nextSemesterClassGradeList = rusaintApi.getClassGradeList(
            session,
            nextSemester.year.toUInt(),
            nextSemester.toRsaintSemesterType()
        ).getOrElse { e ->
            return Result.failure(e)
        }
        Timber.d("test!! $nextSemesterClassGradeList")

        // 있다면 그 학기를 반환
        if (nextSemesterClassGradeList.isNotEmpty()) {
            return Result.success(
                SemesterVO(
                    year = nextSemester.year,
                    semester = nextSemester.storeFormat,
                    semesterRank = 0,
                    semesterStudentCount = 0,
                    overallRank = 0,
                    overallStudentCount = 0,
                    earnedCredit = 0.0f,
                    gpa = 0.0f,
                    totalReportCardId = 1
                )
            )
        }

        // 다음 다음 학기
        val nextNextSemester = when (nextSemester) {
            is SemesterType.One -> SemesterType.Two(nextSemester.year)
            is SemesterType.Summer -> SemesterType.One(nextSemester.year)
            is SemesterType.Two -> SemesterType.Summer(nextSemester.year)
            is SemesterType.Winter -> SemesterType.One(nextSemester.year + 1)
        }
        Timber.d("next next semester: $nextSemester")

        // 다음 다음 학기 수업 성적을 가져온다
        val nextNextSemesterClassGradeList = rusaintApi.getClassGradeList(
            session,
            nextSemester.year.toUInt(),
            nextSemester.toRsaintSemesterType()
        ).getOrElse { e ->
            return Result.failure(e)
        }
        Timber.d("test!! $nextNextSemesterClassGradeList")

        // 있다면 그 학기를 반환
        if (nextNextSemesterClassGradeList.isNotEmpty()) {
            return Result.success(
                SemesterVO(
                    year = nextSemester.year,
                    semester = nextSemester.storeFormat,
                    semesterRank = 0,
                    semesterStudentCount = 0,
                    overallRank = 0,
                    overallStudentCount = 0,
                    earnedCredit = 0.0f,
                    gpa = 0.0f,
                    totalReportCardId = 1
                )
            )
        }

        // 둘 다 없으면 아예 지금까지 나온게 최신인것.
        // courseGradesApplication.semesters(CourseType.BACHELOR)이 가져온게 최신이라는 뜻
        // 그러면 그냥 null을 반환하자.
        return Result.success(null)
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

    private fun String.toSemesterOrder(): Int {
        return when (this) {
            "겨울" -> 4
            "여름" -> 3
            "2" -> 2
            "1" -> 1
            else -> 0
        }
    }
}