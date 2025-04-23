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
        assertEquals(semesterEntity.gpa, 3.0f)
    }

    @Test
    fun getSemesters() = runTest {
        insertSemesters()

        val semesterEntities = semesterDao.getSemesterEntities().first()
        assertEquals(
            listOf(2.0f, 3.0f, 4.0f),
            semesterEntities.map { it.gpa }
        )
    }

    @Test
    fun getSemesters_oneoff(): Unit = runTest {
        insertSemesters()

        val semesterEntities = semesterDao.getOneOffSemesterEntities()
        assertEquals(
            listOf(2.0f, 3.0f, 4.0f),
            semesterEntities.map { it.gpa }
        )
    }

    @Test
    fun upsertSemesters() = runTest {
        insertSemesters()

        val semesterEntities = semesterDao.getOneOffSemesterEntities()
        assertEquals(
            listOf(2.0f, 3.0f, 4.0f),
            semesterEntities.map { it.gpa }
        )

        updateSemesters()

        val replacedSemesterEntities = semesterDao.getOneOffSemesterEntities()
        assertEquals(
            listOf(1.0f, 2.0f, 3.0f),
            replacedSemesterEntities.map { it.gpa }
        )
    }

    @Test
    fun getSemesterWithLectures() = runTest {
        insertSemesters()
        insertLectures()

        val semesterWithLectures = semesterDao.getSemesterWithLectures().first()
        assertEquals(
            listOf(2.0f, 3.0f, 4.0f),
            semesterWithLectures.map { it.semester.gpa }
        )
        assertEquals(
            listOf(listOf("1", "2"), listOf("3"), listOf("4")),
            semesterWithLectures.map { it.lectures.map(LectureEntity::code) }
        )
    }

    @Test
    fun deleteSemester() = runTest {
        insertSemesters()
        insertLectures()

        val lectureEntities1 = lectureDao.getOneOffLectureEntities()
        assertEquals(listOf("1", "2", "3", "4"), lectureEntities1.map(LectureEntity::code))

        semesterDao.deleteAllSemesters()

        val semesterEntities = semesterDao.getOneOffSemesterEntities()
        assert(semesterEntities.isEmpty())

        val lectureEntities2 = lectureDao.getOneOffLectureEntities()
        assert(lectureEntities2.isEmpty())
    }

    private suspend fun insertSemesters() {
        val semesterEntities = listOf(
            testSemesterEntity(2023, SemesterType.Two.name, 2.0f),
            testSemesterEntity(2024, SemesterType.One.name, 3.0f),
            testSemesterEntity(2024, SemesterType.Summer.name, 4.0f),
        )
        semesterDao.insertOrIgnoreSemesters(semesterEntities)
    }

    private suspend fun updateSemesters() {
        val semesterEntities = listOf(
            testSemesterEntity(2023, SemesterType.Two.name, 1.0f),
            testSemesterEntity(2024, SemesterType.One.name, 2.0f),
            testSemesterEntity(2024, SemesterType.Summer.name, 3.0f),
        )
        semesterDao.upsertSemesters(semesterEntities)
    }

    private suspend fun insertLectures() {
        val lectureEntities = listOf(
            testLectureEntity(2023, SemesterType.Two.name, "", code = "1"),
            testLectureEntity(2023, SemesterType.Two.name, "", code = "2"),
            testLectureEntity(2024, SemesterType.One.name, "", code = "3"),
            testLectureEntity(2024, SemesterType.Summer.name, "", code = "4"),
        )
        lectureDao.insertOrIgnoreLectures(lectureEntities)
    }
}
