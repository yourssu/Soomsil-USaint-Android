package com.yourssu.soomsil.usaint

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yourssu.soomsil.usaint.data.source.local.AppDatabase
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.dao.TotalReportCardDao
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCardVO
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DaoTest {

    private lateinit var db: AppDatabase
    private lateinit var totalReportCardDao: TotalReportCardDao
    private lateinit var semesterDao: SemesterDao
    private lateinit var lectureDao: LectureDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        totalReportCardDao = db.totalReportCardDao()
        semesterDao = db.semesterDao()
        lectureDao = db.lectureDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun totalReportCardInsertAndFetchTest(): Unit = runBlocking {
        // TotalReportCard 삽입
        val totalReportCard = TotalReportCardVO(
            id = 1,
            earnedCredit = 120f,
            gpa = 3.8f
        )
        totalReportCardDao.insertTotalReportCard(totalReportCard)

        // TotalReportCard 조회
        val fetched = totalReportCardDao.getTotalReportCard()

        // TotalReportCard 비교
        assertThat(fetched, equalTo(totalReportCard))
    }

    @Test
    fun semesterInsertAndReplaceTest(): Unit = runBlocking {
        // TotalReportCard 삽입
        val totalReportCard = TotalReportCardVO(
            id = 1,
            earnedCredit = 120f,
            gpa = 3.8f
        )
        totalReportCardDao.insertTotalReportCard(totalReportCard)

        // Semester 삽입
        val semester1 = SemesterVO(
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
        semesterDao.insertSemester(semester1)

        // Semester 조회
        val fetched1 = semesterDao.getSemesterByYearAndSemester(2024, "1학기")

        // Semester 비교
        assertThat(fetched1?.year, equalTo(2024))
        assertThat(fetched1?.semester, equalTo("1학기"))
        assertThat(fetched1?.gpa, equalTo(3.9f))

        // 같은 (year, semester)로 다른 데이터 삽입 -> REPLACE
        val semester2 = semester1.copy(
            semesterRank = 5,
            gpa = 4.0f
        )
        semesterDao.insertSemester(semester2)

        // REPLACE된 Semester 조회
        val fetched2 = semesterDao.getSemesterByYearAndSemester(2024, "1학기")

        // REPLACE된 Semester 비교
        assertThat(fetched2?.semesterRank, equalTo(5))
        assertThat(fetched2?.gpa, equalTo(4.0f))
    }

    @Test
    fun lectureInsertAndUniqueReplaceTest(): Unit = runBlocking {
        // TotalReportCard 삽입
        val totalReportCard = TotalReportCardVO(
            id = 1,
            earnedCredit = 120f,
            gpa = 3.8f
        )
        totalReportCardDao.insertTotalReportCard(totalReportCard)

        // Semester를 하나 생성 (2024-1학기)
        val semester = SemesterVO(
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
        // Semester 삽입
        semesterDao.insertSemester(semester)
        // 삽입된 Semester 조회
        val insertedSemester = semesterDao.getSemesterByYearAndSemester(2024, "1학기")!!

        // Lecture 삽입
        val lecture1 = LectureVO(
            id = 0,
            title = "Data Structures",
            code = "CS101",
            credit = 3f,
            grade = "A+",
            score = "95",
            professorName = "Dr. Kim",
            semesterId = insertedSemester.id
        )
        lectureDao.insertLecture(lecture1)

        // Lecture 조회
        val fetchedLecture1 = lectureDao.getLectureByCode("CS101")
        assertThat(fetchedLecture1?.title, equalTo("Data Structures"))

        // 동일한 code로 다른 Lecture 데이터 삽입 -> REPLACE
        val lecture2 = lecture1.copy(
            title = "Advanced Data Structures",
            credit = 4f,
            grade = "A"
        )
        lectureDao.insertLecture(lecture2)

        // REPLACE된 Lecture 조회
        val fetchedLecture2 = lectureDao.getLectureByCode("CS101")
        assertThat(fetchedLecture2?.title, equalTo("Advanced Data Structures"))
        assertThat(fetchedLecture2?.credit, equalTo(4f))
        assertThat(fetchedLecture2?.grade, equalTo("A"))
    }

    @Test
    fun totalReportCardWithSemestersTest(): Unit = runBlocking {
        // TotalReportCard 삽입
        totalReportCardDao.insertTotalReportCard(TotalReportCardVO(id = 1, 100f, 3.5f))

        // Semester 삽입
        val a = semesterDao.insertSemester(
            SemesterVO(
                id = 0,
                year = 2023,
                semester = "1학기",
                semesterRank = 1,
                semesterStudentCount = 100,
                overallRank = 10,
                overallStudentCount = 500,
                earnedCredit = 20f,
                gpa = 3.8f,
                totalReportCardId = 1
            )
        )
        // Lecture 삽입
        val lecture1 = LectureVO(
            title = "Data Structures",
            code = "CS101",
            credit = 3f,
            grade = "A+",
            score = "95",
            professorName = "Dr. Kim",
            semesterId = a.toInt()
        )
        // Lecture 삽입
        val lecture2 = LectureVO(
            title = "Advanced Data Structures",
            code = "CS102",
            credit = 4f,
            grade = "A",
            score = "92",
            professorName = "Dr. Lee",
            semesterId = a.toInt()
        )
        lectureDao.insertLecture(lecture1)
        lectureDao.insertLecture(lecture2)

        val result = totalReportCardDao.getTotalReportCardWithSemesters()
        println(result?.semesters)
        assertThat(result?.semesters?.size, equalTo(1))
    }
}