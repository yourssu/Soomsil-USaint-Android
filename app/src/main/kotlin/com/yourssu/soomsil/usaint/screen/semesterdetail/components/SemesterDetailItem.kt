package com.yourssu.soomsil.usaint.screen.semesterdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.domain.type.makeSemesterType
import com.yourssu.soomsil.usaint.screen.semesterdetail.CaptureFlag
import com.yourssu.soomsil.usaint.ui.entities.LectureInfo
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.Tier
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun SemesterDetailItem(
    semester: Semester,
    modifier: Modifier = Modifier,
    captureFlag: CaptureFlag = CaptureFlag.None,
    lectureInfos: List<LectureInfo> = emptyList(),
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
//            .verticalScroll(rememberScrollState()), // Capturable 내에서 scroll 사용 불가 (unbound = true)
        verticalArrangement = Arrangement.Top,
    ) {
        GradeSummary(
            semesterName = semester.type.fullName,
            gpa = semester.gpa,
            earnedCredit = semester.earnedCredit,
            semesterRank = semester.semesterRank,
            semesterMaxRank = semester.semesterStudentCount,
            overallRank = semester.overallRank,
            overallMaxRank = semester.overallStudentCount,
            captureFlag = captureFlag,
        )

        HorizontalDivider(Modifier.padding(horizontal = 16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (lectureInfos.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(20.dp),
                )
            } else {
                lectureInfos.forEach { course ->
                    CourseGradeItem(
                        tier = course.tier,
                        courseName = course.name,
                        professor = course.professorName,
                        courseCredit = course.credit,
                        captureFlag = captureFlag,
                    )
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun SemesterDetailItemPreview() {
    val tiers = listOf(
        "A+", "A0", "A-",
        "B+", "B0", "B-",
        "C+", "C0", "C-",
        "D+", "D0", "D-",
        "P", "F",
    )
    SoomsilUSaintTheme {
        SemesterDetailItem(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            semester = Semester(
                makeSemesterType(2020, "1"),
                gpa = 4.06.toGrade(),
                earnedCredit = 17.5.toCredit(),
                semesterRank = 15,
                semesterStudentCount = 55,
                overallRank = 12,
                overallStudentCount = 100,
            ),
            lectureInfos = tiers.map { tier ->
                LectureInfo(
                    tier = Tier(tier),
                    name = "가나다라",
                    credit = 3.toCredit(),
                    professorName = tier,
                )
            },
        )
    }
}

@PreviewLightDark
@Composable
private fun SemesterDetailItemPreview_empty() {
    SoomsilUSaintTheme {
        SemesterDetailItem(
            semester = Semester(
                makeSemesterType(2020, "1"),
                gpa = 4.06.toGrade(),
                earnedCredit = 17.5.toCredit(),
                semesterRank = 15,
                semesterStudentCount = 55,
                overallRank = 12,
                overallStudentCount = 100,
            ),
        )
    }
}
