package com.yourssu.soomsil.usaint

import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LectureDaoTest : DatabaseTest() {

    @Test
    fun upsertLectures() = runTest {
        insertSemesters()
        insertLectures()

        val semesterWithLectures1 = semesterDao.getSemesterWithLectures().first()
        assertEquals(
            listOf(2.0f, 3.0f, 4.0f),
            semesterWithLectures1.keys.map { it.gpa }
        )
        assertEquals(
            listOf(
                listOf("Lecture 2023-2-1", "Lecture 2023-2-2"),
                listOf("Lecture 2024-1"),
                listOf("Lecture 2024-Summer")
            ),
            semesterWithLectures1.values.map {
                it.map(LectureEntity::title)
            }
        )
        assertEquals(
            listOf(
                listOf(mapOf("중간고사" to "90", "기말고사" to "80"), emptyMap()),
                listOf(emptyMap()),
                listOf(emptyMap()),
            ),
            semesterWithLectures1.values.map {
                it.map(LectureEntity::detail)
            }
        )

        updateLectures()

        val semesterWithLectures2 = semesterDao.getSemesterWithLectures().first()
        assertEquals(
            listOf(
                listOf("Lecture 2023-2-1 updated", "Lecture 2023-2-2"),
                listOf("Lecture 2024-1 updated"),
                listOf("Lecture 2024-Summer", "Lecture 2024-Summer added")
            ),
            semesterWithLectures2.values.map {
                it.map(LectureEntity::title)
            }
        )
    }

    @Test
    fun deleteLecture() = runTest {
        insertSemesters()
        insertLectures()

        val lectureEntities1 = lectureDao.getOneOffLectureEntities()
        assertEquals(listOf("1", "2", "3", "4"), lectureEntities1.map(LectureEntity::code))

        lectureDao.deleteLectureEntitiesWithYearSemester(2023, SemesterType.Two.name)

        val lectureEntities2 = lectureDao.getOneOffLectureEntities()
        assertEquals(listOf("3", "4"), lectureEntities2.map(LectureEntity::code))
    }

    private suspend fun insertSemesters() {
        val semesterEntities = listOf(
            testSemesterEntity(2023, SemesterType.Two.name, 2.0f),
            testSemesterEntity(2024, SemesterType.One.name, 3.0f),
            testSemesterEntity(2024, SemesterType.Summer.name, 4.0f),
        )
        semesterDao.insertOrIgnoreSemesters(semesterEntities)
    }

    private suspend fun insertLectures() {
        val lectureEntities = listOf(
            testLectureEntity(
                2023,
                SemesterType.Two.name,
                "Lecture 2023-2-1",
                code = "1",
                detail = mapOf("중간고사" to "90", "기말고사" to "80")
            ),
            testLectureEntity(2023, SemesterType.Two.name, "Lecture 2023-2-2", code = "2"),
            testLectureEntity(2024, SemesterType.One.name, "Lecture 2024-1", code = "3"),
            testLectureEntity(2024, SemesterType.Summer.name, "Lecture 2024-Summer", code = "4"),
        )
        lectureDao.insertOrIgnoreLectures(lectureEntities)
    }

    private suspend fun updateLectures() {
        val lectureEntities = listOf(
            testLectureEntity(2023, SemesterType.Two.name, "Lecture 2023-2-1 updated", code = "1"),
            testLectureEntity(2024, SemesterType.One.name, "Lecture 2024-1 updated", code = "3"),
            testLectureEntity(
                2024,
                SemesterType.Summer.name,
                "Lecture 2024-Summer added",
                code = "5"
            ),
        )
        lectureDao.upsertLectures(lectureEntities)
    }
}