package com.yourssu.soomsil.usaint

import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.domain.type.LectureDiff
import com.yourssu.soomsil.usaint.domain.type.LectureDiffOption
import com.yourssu.soomsil.usaint.domain.usecase.LecturesDiffUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class LecturesDiffUseCaseTest {
    val diffUseCase = LecturesDiffUseCase()
    val oldList = listOf(
        LectureVO(
            title = "title1",
            code = "21500123",
            credit = 3f,
            grade = "성적 미입력",
            score = "Empty",
            professorName = "",
            semesterId = 0,
        ),
        LectureVO(
            title = "title2",
            code = "21500199",
            credit = 2f,
            grade = "B+",
            score = "89",
            professorName = "",
            semesterId = 0,
        )
    )
    val newList = listOf(
        LectureVO(
            title = "title1",
            code = "21500123",
            credit = 3f,
            grade = "A+",
            score = "98",
            professorName = "",
            semesterId = 0,
        ),
        LectureVO(
            title = "title2",
            code = "21500199",
            credit = 2f,
            grade = "B+",
            score = "89",
            professorName = "",
            semesterId = 0,
        )
    )

    @Test
    fun test() {
        val diffOption = diffUseCase(oldList, newList)
        assert(diffOption is LectureDiffOption.Some)

        diffOption as LectureDiffOption.Some

        assertEquals(
            listOf(
                LectureDiff(
                    title = "title1",
                    code = "21500123",
                    credit = null,
                    grade = "성적 미입력" to "A+",
                    score = "Empty" to "98",
                )
            ), diffOption.diff
        )
    }

    @Test
    fun `같은 리스트는 차이가 없음`() {
        assert(diffUseCase(oldList, oldList) is LectureDiffOption.None)
    }

    @Test
    fun stress_test() {
        val before = listOf(
            LectureVO(
                title = "title1",
                code = "21500001",
                credit = 3f,
                grade = "A+",
                score = "98",
                professorName = "",
                semesterId = 0,
            ),
            LectureVO(
                title = "title3",
                code = "21500123",
                credit = 2f,
                grade = "A0",
                score = "95",
                professorName = "",
                semesterId = 0,
            ),
            LectureVO(
                title = "title2",
                code = "21500002",
                credit = 0.5f,
                grade = "성적 미입력",
                score = "Empty",
                professorName = "",
                semesterId = 0,
            ),
            LectureVO(
                title = "title5",
                code = "21500125",
                credit = 3f,
                grade = "성적 미입력",
                score = "Empty",
                professorName = "",
                semesterId = 0,
            ),
            LectureVO(
                title = "title4",
                code = "21500124",
                credit = 3f,
                grade = "B+",
                score = "88",
                professorName = "",
                semesterId = 0,
            ),
        )
        val after = listOf(
            LectureVO(
                title = "title1",
                code = "21500001",
                credit = 3f,
                grade = "A+",
                score = "98",
                professorName = "",
                semesterId = 0,
            ),
            LectureVO(
                title = "title3",
                code = "21500123",
                credit = 3f,
                grade = "A0",
                score = "95",
                professorName = "",
                semesterId = 0,
            ),
            LectureVO(
                title = "title2",
                code = "21500002",
                credit = 0.5f,
                grade = "P",
                score = "Pass",
                professorName = "",
                semesterId = 0,
            ),
            LectureVO(
                title = "title5",
                code = "21500125",
                credit = 3f,
                grade = "A-",
                score = "90",
                professorName = "",
                semesterId = 0,
            ),
            LectureVO(
                title = "title4",
                code = "21500124",
                credit = 3f,
                grade = "A-",
                score = "91",
                professorName = "",
                semesterId = 0,
            ),
        )

        val resultOption = diffUseCase(before, after)
        assert(resultOption is LectureDiffOption.Some)

        val expectation = listOf(
            LectureDiff(
                title = "title2",
                code = "21500002",
                credit = null,
                grade = "성적 미입력" to "P",
                score = "Empty" to "Pass",
            ),
            LectureDiff(
                title = "title3",
                code = "21500123",
                credit = 2.0f to 3.0f,
                grade = null,
                score = null,
            ),
            LectureDiff(
                title = "title4",
                code = "21500124",
                credit = null,
                grade = "B+" to "A-",
                score = "88" to "91",
            ),
            LectureDiff(
                title = "title5",
                code = "21500125",
                credit = null,
                grade = "성적 미입력" to "A-",
                score = "Empty" to "90",
            )
        )

        resultOption as LectureDiffOption.Some
        assertEquals(expectation, resultOption.diff)
    }
}