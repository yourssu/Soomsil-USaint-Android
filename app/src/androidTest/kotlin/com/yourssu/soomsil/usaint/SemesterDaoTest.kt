package com.yourssu.soomsil.usaint

import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SemesterDaoTest : DatabaseTest() {

    @Test
    fun getSemester() = runTest {
        insertSemesters()

        val semesterEntity = semesterDao.getSemesterEntity(2024, SemesterType.One.name).first()
        assertEquals(semesterEntity.gpa, 3.8f)
    }

    @Test
    fun getSemesters() = runTest {
        insertSemesters()

        val semesterEntities = semesterDao.getSemesterEntities().first()
        assertEquals(
            listOf(4.0f, 3.8f, 4.5f),
            semesterEntities.map { it.gpa }
        )
    }

    @Test
    fun getSemesters_oneoff(): Unit = runTest {
        insertSemesters()

        val semesterEntities = semesterDao.getOneOffSemesterEntities()
        assertEquals(
            listOf(4.0f, 3.8f, 4.5f),
            semesterEntities.map { it.gpa }
        )
    }

    @Test
    fun upsertSemesters() = runTest {
        insertSemesters()

        val semesterEntities = semesterDao.getOneOffSemesterEntities()
        assertEquals(
            listOf(4.0f, 3.8f, 4.5f),
            semesterEntities.map { it.gpa }
        )

        updateSemesters()

        val replacedSemesterEntities = semesterDao.getOneOffSemesterEntities()
        assertEquals(
            listOf(2.0f, 3.0f, 4.0f),
            replacedSemesterEntities.map { it.gpa }
        )
    }

    @Test
    fun getSemesterWithLectures() = runTest {
        insertSemesters()
        insertLectures()

        val semesterWithLectures = semesterDao.getSemesterWithLectures().first()
        assertEquals(
            listOf(4.0f, 3.8f, 4.5f),
            semesterWithLectures.map { it.semester.gpa }
        )
        assertEquals(
            listOf(
                listOf("Lecture 2023-2-1", "Lecture 2023-2-2"),
                listOf("Lecture 2024-1"),
                listOf("Lecture 2024-Summer")
            ),
            semesterWithLectures.map {
                it.lectures.map(LectureEntity::title)
            }
        )
    }

    private suspend fun insertSemesters() {
        val semesterEntities = listOf(
            testSemesterEntity(2023, SemesterType.Two.name, 4.0f),
            testSemesterEntity(2024, SemesterType.One.name, 3.8f),
            testSemesterEntity(2024, SemesterType.Summer.name, 4.5f),
        )
        semesterDao.insertOrIgnoreSemesters(semesterEntities)
    }

    private suspend fun updateSemesters() {
        val semesterEntities = listOf(
            testSemesterEntity(2023, SemesterType.Two.name, 2.0f),
            testSemesterEntity(2024, SemesterType.One.name, 3.0f),
            testSemesterEntity(2024, SemesterType.Summer.name, 4.0f),
        )
        semesterDao.upsertSemesters(semesterEntities)
    }

    private suspend fun insertLectures() {
        val lectureEntities = listOf(
            testLectureEntity(2023, SemesterType.Two.name, "Lecture 2023-2-1", code = "1"),
            testLectureEntity(2023, SemesterType.Two.name, "Lecture 2023-2-2", code = "2"),
            testLectureEntity(2024, SemesterType.One.name, "Lecture 2024-1", code = "3"),
            testLectureEntity(2024, SemesterType.Summer.name, "Lecture 2024-Summer", code = "4"),
        )
        lectureDao.insertOrIgnoreLectures(lectureEntities)
    }
}

