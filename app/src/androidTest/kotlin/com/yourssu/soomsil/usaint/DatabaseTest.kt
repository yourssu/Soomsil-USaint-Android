package com.yourssu.soomsil.usaint

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.yourssu.soomsil.usaint.data.source.local.AppDatabase
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterEntity
import org.junit.After
import org.junit.Before

abstract class DatabaseTest {
    private lateinit var db: AppDatabase
    lateinit var semesterDao: SemesterDao
    lateinit var lectureDao: LectureDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        semesterDao = db.semesterDao()
        lectureDao = db.lectureDao()
    }

    @After
    fun teardown() {
        db.close()
    }
}

fun testSemesterEntity(
    year: Int,
    semester: String,
    gpa: Float,
) = SemesterEntity(
    year = year,
    semester = semester,
    semesterRank = 0,
    semesterStudentCount = 0,
    overallRank = 0,
    overallStudentCount = 0,
    earnedCredit = 0f,
    gpa = gpa,
)

fun testLectureEntity(
    year: Int,
    semester: String,
    title: String,
    code: String,
    detail: Map<String, String> = emptyMap(),
) = LectureEntity(
    year = year,
    semester = semester,
    title = title,
    code = code,
    credit = 0f,
    grade = "",
    score = "",
    professorName = "",
    detail = detail,
)
