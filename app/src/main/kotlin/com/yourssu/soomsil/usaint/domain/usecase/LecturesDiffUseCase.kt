package com.yourssu.soomsil.usaint.domain.usecase

import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.LectureGrade
import com.yourssu.soomsil.usaint.core.model.LectureScore
import com.yourssu.soomsil.usaint.core.model.Unknown
import javax.inject.Inject

// TODO LectureData로 변경

// Pair<before, after>
data class LectureDiff(
    val title: String,
    val code: String,
    val credit: Pair<Float, Float>?,
    val grade: Pair<LectureGrade, LectureGrade>?,
    val score: Pair<LectureScore, LectureScore>?,
)

fun LectureData.diff(other: LectureData) = LectureDiff(
    title = title,
    code = code,
    credit = if (credit == other.credit) null else (credit to other.credit),
    grade = if (lectureGrade == other.lectureGrade) null else (lectureGrade to other.lectureGrade),
    score = if (lectureScore == other.lectureScore) null else (lectureScore to other.lectureScore),
)

class LecturesDiffUseCase @Inject constructor() {
    operator fun invoke(old: List<LectureData>, new: List<LectureData>): List<LectureDiff> {
        val oldSorted = old.sortedBy { it.code }
        val newSorted = new.sortedBy { it.code }
        val diff = ArrayList<LectureDiff>()
        var i = 0
        var j = 0

        while (i in oldSorted.indices && j in newSorted.indices) {
            val o = oldSorted[i]
            val n = newSorted[j]
            if (o.code == n.code) {
                if (o != n) diff.add(o.diff(n))
                i++
                j++
            } else if (o.code < n.code) {
                diff.add(
                    LectureDiff(
                        title = o.title,
                        code = o.code,
                        credit = o.credit to 0f,
                        grade = o.lectureGrade to Unknown,
                        score = o.lectureScore to Unknown,
                    )
                )
                i++
            } else {
                diff.add(
                    LectureDiff(
                        title = n.title,
                        code = n.code,
                        credit = 0f to n.credit,
                        grade = Unknown to n.lectureGrade,
                        score = Unknown to n.lectureScore,
                    )
                )
                j++
            }
        }

        if (i != oldSorted.size) {
            diff.addAll(oldSorted.subList(i, oldSorted.size).map {
                LectureDiff(
                    title = it.title,
                    code = it.code,
                    credit = it.credit to 0f,
                    grade = it.lectureGrade to Unknown,
                    score = it.lectureScore to Unknown,
                )
            })
        } else if (j != newSorted.size) {
            diff.addAll(newSorted.subList(j, newSorted.size).map {
                LectureDiff(
                    title = it.title,
                    code = it.code,
                    credit = 0f to it.credit,
                    grade = Unknown to it.lectureGrade,
                    score = Unknown to it.lectureScore,
                )
            })
        }

        return diff
    }
}