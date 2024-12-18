package com.yourssu.soomsil.usaint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.source.local.entity.Lecture
import com.yourssu.soomsil.usaint.data.source.local.entity.Semester
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportCardViewModel @Inject constructor(
    private val repository: ReportCardRepository
) : ViewModel() {
    fun testOperations() {
        viewModelScope.launch {
            // === 테스트 데이터 삽입 ===
            // TotalReportCard 삽입 (id=1 고정)
            val totalReportCard = TotalReportCard(
                id = 1,
                earnedCredit = 120f,
                gpa = 3.8f
            )
            repository.upsertTotalReportCard(totalReportCard)

            // Semester 삽입
            val semesterSpring = Semester(
                id = 0,
                year = 2024,
                semester = "1학기",
                semesterRank = 10,
                semesterStudentCount = 200,
                overallRank = 15,
                overallStudentCount = 1000,
                earnedCredit = 18f,
                gpa = 3.9f,
                totalReportCardId = 1
            )
            repository.upsertSemester(semesterSpring)

            val semesterFall = Semester(
                id = 0,
                year = 2024,
                semester = "2학기",
                semesterRank = 12,
                semesterStudentCount = 200,
                overallRank = 17,
                overallStudentCount = 1000,
                earnedCredit = 18f,
                gpa = 3.7f,
                totalReportCardId = 1
            )
            repository.upsertSemester(semesterFall)

            // Lecture 삽입 (2024-1학기)
            val lecture1 = Lecture(
                id = 0,
                title = "Data Structures",
                code = "CS101",
                credit = 3f,
                grade = "A+",
                score = "95",
                professorName = "Dr. Kim",
                semesterId = getSemesterId(year = 2024, semesterName = "1학기") // 아래에서 Helper 함수 사용
            )
            repository.upsertLecture(lecture1)

            val lecture2 = Lecture(
                id = 0,
                title = "Algorithms",
                code = "CS102",
                credit = 3f,
                grade = "A",
                score = "90",
                professorName = "Dr. Lee",
                semesterId = getSemesterId(year = 2024, semesterName = "1학기")
            )
            repository.upsertLecture(lecture2)

            // === 데이터 조회 ===

            // 1) getTotalReportCard
            val total = repository.getTotalReportCard()
            println("TotalReportCard: $total")

            // 2) getTotalReportCardWithSemesters
            val totalWithSemesters = repository.getTotalReportCardWithSemesters()
            println("TotalReportCardWithSemesters: $totalWithSemesters")

            // 3) getSemester
            val springSemester = repository.getSemester(2024, "1학기")
            println("Spring Semester: $springSemester")

            // 4) getSemestersByTotalReportCardId
            val semesters = repository.getSemestersByTotalReportCardId(1)
            println("Semesters for TotalReportCard=1: $semesters")

            // 5) getSemesterWithLectures
            val semesterWithLectures = repository.getSemesterWithLectures(2024, "1학기")
            println("2024-1학기 with Lectures: $semesterWithLectures")

            // Lecture 관련 조회
            val lectureFetched = repository.getLectureByCode("CS101")
            println("Lecture by code CS101: $lectureFetched")

            // getLecturesBySemesterId
            val semesterId = getSemesterId(2024, "1학기")
            val lecturesForSemester = repository.getLecturesBySemesterId(semesterId)
            println("Lectures for 2024-1학기: $lecturesForSemester")
        }
    }

    // 중복 데이터 삽입 테스트
    fun testDuplicateHandling() {
        viewModelScope.launch {
            println("=== 중복 데이터 처리 테스트 시작 ===")

            // 1. TotalReportCard 중복 삽입 테스트
            val totalReportCard1 = TotalReportCard(
                id = 1, // 고정된 Primary Key
                earnedCredit = 120f,
                gpa = 3.8f
            )
            repository.upsertTotalReportCard(totalReportCard1)
            println("첫 번째 TotalReportCard 삽입 완료: $totalReportCard1")

            val totalReportCard2 = TotalReportCard(
                id = 1,
                earnedCredit = 130f, // 수정된 데이터
                gpa = 3.9f
            )
            repository.upsertTotalReportCard(totalReportCard2)
            println("두 번째 TotalReportCard 삽입 완료 (덮어쓰기): $totalReportCard2")

            val resultTotal = repository.getTotalReportCard()
            println("TotalReportCard 최종 결과: $resultTotal")

            // 2. Semester 중복 삽입 테스트
            val semester1 = Semester(
                id = 0,
                year = 2024,
                semester = "1학기",
                semesterRank = 1,
                semesterStudentCount = 100,
                overallRank = 10,
                overallStudentCount = 500,
                earnedCredit = 18f,
                gpa = 3.8f,
                totalReportCardId = 1
            )
            repository.upsertSemester(semester1)
            println("첫 번째 Semester 삽입 완료: $semester1")

            val semester2 = Semester(
                id = 0, // id는 autoGenerate, 대신 year + semester로 unique
                year = 2024,
                semester = "1학기",
                semesterRank = 2, // 수정된 데이터
                semesterStudentCount = 150,
                overallRank = 15,
                overallStudentCount = 600,
                earnedCredit = 20f,
                gpa = 3.9f,
                totalReportCardId = 1
            )
            repository.upsertSemester(semester2)
            println("두 번째 Semester 삽입 완료 (덮어쓰기): $semester2")

            val semesterResults = repository.getSemester(2024, "1학기")
            println("Semester 최종 결과: $semesterResults")

            // 3. Lecture 중복 삽입 테스트
            val lecture1 = Lecture(
                id = 0,
                title = "Data Structures",
                code = "CS101", // unique index
                credit = 3f,
                grade = "A+",
                score = "95",
                professorName = "Dr. Kim",
                semesterId = semesterResults?.id ?: 0
            )
            repository.upsertLecture(lecture1)
            println("첫 번째 Lecture 삽입 완료: $lecture1")

            val lecture2 = Lecture(
                id = 0,
                title = "Advanced Data Structures", // 변경된 데이터
                code = "CS101", // 동일한 code (unique constraint)
                credit = 4f,
                grade = "A",
                score = "92",
                professorName = "Dr. Lee",
                semesterId = semesterResults?.id ?: 0
            )
            repository.upsertLecture(lecture2)
            println("두 번째 Lecture 삽입 완료 (덮어쓰기): $lecture2")

            val lectureResult = repository.getLectureByCode("CS101")
            println("Lecture 최종 결과: $lectureResult")

            println("=== 중복 데이터 처리 테스트 종료 ===")
        }
    }

    // Helper 함수: year와 semesterName으로 Semester를 조회한 뒤 semesterId 반환
    private suspend fun getSemesterId(year: Int, semesterName: String): Int {
        val semester = repository.getSemester(year, semesterName)
        return semester?.id ?: throw IllegalStateException("Semester not found for $year-$semesterName")
    }
}