package com.yourssu.soomsil.usaint.screen.semesterdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.Divider
import com.yourssu.design.system.compose.atom.Thickness
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.domain.type.makeSemesterType
import com.yourssu.soomsil.usaint.ui.entities.Credit
import com.yourssu.soomsil.usaint.ui.entities.Grade
import com.yourssu.soomsil.usaint.ui.entities.LectureInfo
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.Tier
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import java.text.DecimalFormat

@Composable
fun SemesterDetailItem(
    semester: Semester,
    modifier: Modifier = Modifier,
    captureFlag: CaptureFlag = CaptureFlag.None,
    lectureInfos: List<LectureInfo> = emptyList(),
) {
    Column(
        modifier = modifier
            .background(YdsTheme.colors.bgNormal),
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

        Divider(
            thickness = Thickness.Thin,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (lectureInfos.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(20.dp),
                    color = YdsTheme.colors.buttonPoint
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

@Composable
private fun GradeSummary(
    semesterName: String,
    gpa: Grade,
    earnedCredit: Credit,   // 취득 학점
    semesterRank: Int,      // 학기별 석차
    semesterMaxRank: Int,
    overallRank: Int,       // 전체 석차
    overallMaxRank: Int,
    captureFlag: CaptureFlag,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(bottom = 12.dp),
    ) {
        Column(
            modifier = Modifier.padding(
                top = 20.dp,
                start = 24.dp,
                bottom = 12.dp,
            ),
        ) {
            if (captureFlag !is CaptureFlag.None) {
                // 캡처 화면에서 필요함
                YdsText(
                    text = semesterName,
                    style = YdsTheme.typography.button2,
                    color = YdsTheme.colors.textSecondary,
                )
                Spacer(Modifier.height(2.dp))
            }
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                YdsText(
                    text = gpa.formatToString(),
                    style = YdsTheme.typography.display1,
                )
                YdsText(
                    text = stringResource(id = R.string.grade_delimiter),
                    style = YdsTheme.typography.body1,
                    color = YdsTheme.colors.textTertiary,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                YdsText(
                    text = Grade.Max.formatToString(),
                    style = YdsTheme.typography.body1,
                    color = YdsTheme.colors.textTertiary,
                )
            }
        }
        SummaryList(
            title = stringResource(id = R.string.gradelist_summary_credit),
            actualValue = earnedCredit.value,
            modifier = Modifier.fillMaxWidth(),
        )
        SummaryList(
            title = stringResource(id = R.string.gradelist_summary_rank),
            actualValue = semesterRank.toFloat(),
            maxValue = semesterMaxRank,
            modifier = Modifier.fillMaxWidth(),
        )
        SummaryList(
            title = stringResource(id = R.string.gradelist_summary_overall_rank),
            actualValue = overallRank.toFloat(),
            maxValue = overallMaxRank,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SummaryList(
    title: String,
    actualValue: Float,
    modifier: Modifier = Modifier,
    maxValue: Int = 0,
) {
    Row(
        modifier = modifier
            .padding(
                horizontal = 28.dp,
                vertical = 8.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YdsText(
            text = title,
            style = YdsTheme.typography.body1,
            color = YdsTheme.colors.textTertiary,
            modifier = Modifier.weight(1f),
        )
        YdsText(
            text = if (actualValue > 0) {
                DecimalFormat("0.##").format(actualValue) // 하위 소수점 0 버림
            } else {
                "-"
            },
            style = YdsTheme.typography.subTitle2,
            color = YdsTheme.colors.textSecondary,
        )
        if (maxValue > 0) {
            YdsText(
                text = stringResource(id = R.string.grade_delimiter),
                style = YdsTheme.typography.button4,
                color = YdsTheme.colors.textTertiary,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            YdsText(
                text = maxValue.toString(),
                style = YdsTheme.typography.button4,
                color = YdsTheme.colors.textTertiary,
            )
        }
    }
}

@Composable
private fun CourseGradeItem(
    tier: Tier,
    courseName: String,
    professor: String,
    courseCredit: Credit,
    captureFlag: CaptureFlag,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.padding(
                top = 16.dp,
                bottom = 16.dp,
                end = 16.dp,
            ),
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = tier.id),
                contentScale = ContentScale.Fit,
                contentDescription = "tier",
            )
        }
        if (captureFlag is CaptureFlag.HidingInfo) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(25.dp)
                    .background(YdsTheme.colors.bgSelected),
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp),
            ) {
                YdsText(
                    text = courseName,
                    style = YdsTheme.typography.subTitle2,
                )
                YdsText(
                    text = "$professor · ${courseCredit.formatToString()}학점",
                    style = YdsTheme.typography.button3,
                    color = YdsTheme.colors.textTertiary,
                )
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
    YdsTheme {
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
    YdsTheme {
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
