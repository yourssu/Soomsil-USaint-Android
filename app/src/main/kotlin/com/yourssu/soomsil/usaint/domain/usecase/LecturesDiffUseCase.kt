package com.yourssu.soomsil.usaint.domain.usecase

import com.yourssu.soomsil.usaint.data.source.local.entity.LectureVO
import com.yourssu.soomsil.usaint.domain.type.LectureDiff
import com.yourssu.soomsil.usaint.domain.type.LectureDiffOption
import com.yourssu.soomsil.usaint.domain.type.diff
import javax.inject.Inject

class LecturesDiffUseCase @Inject constructor() {
    operator fun invoke(old: List<LectureVO>, new: List<LectureVO>): LectureDiffOption {
        val oldSorted = old.sortedBy { it.code }
        val newSorted = new.sortedBy { it.code }
        val diff = ArrayList<LectureDiff>()
        var i = 0
        var j = 0

        while (i in oldSorted.indices && j in newSorted.indices) {
            val o = oldSorted[i]
            val n = newSorted[j]
            if (o.code == n.code) {
                if (!o.equalsIgnoreIds(n)) diff.add(o.diff(n))
                i++
                j++
            } else if (o.code < n.code) {
                diff.add(
                    LectureDiff(
                        title = o.title,
                        code = o.code,
                        credit = o.credit to 0f,
                        grade = o.grade to "",
                        score = o.score to "",
                    )
                )
                i++
            } else {
                diff.add(
                    LectureDiff(
                        title = n.title,
                        code = n.code,
                        credit = 0f to n.credit,
                        grade = "" to n.grade,
                        score = "" to n.score,
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
                    grade = it.grade to "",
                    score = it.score to "",
                )
            })
        } else if (j != newSorted.size) {
            diff.addAll(newSorted.subList(j, newSorted.size).map {
                LectureDiff(
                    title = it.title,
                    code = it.code,
                    credit = 0f to it.credit,
                    grade = "" to it.grade,
                    score = "" to it.score,
                )
            })
        }

        return if (diff.isEmpty()) LectureDiffOption.None else LectureDiffOption.Some(diff)
    }
}