package com.yourssu.soomsil.usaint.screen.reportcard.components

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
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.domain.type.makeSemesterType
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import com.yourssu.soomsil.usaint.ui.types.LectureInfo
import com.yourssu.soomsil.usaint.ui.types.Semester
import com.yourssu.soomsil.usaint.ui.types.Tier
import com.yourssu.soomsil.usaint.ui.types.toCredit
import com.yourssu.soomsil.usaint.ui.types.toGrade

@Composable
fun SemesterDetailItem(
    semester: SemesterData,
    modifier: Modifier = Modifier,
//    captureFlag: CaptureFlag = CaptureFlag.None,
    lectureDataList: List<LectureData> = emptyList(),
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
//            .verticalScroll(rememberScrollState()), // Capturable 내에서 scroll 사용 불가 (unbound = true)
        verticalArrangement = Arrangement.Top,
    ) {
        GradeSummary(
            semesterName = "${semester.year} ${semester.semester.kor}학기",
            gpa = semester.gradePointsAverage,
            earnedCredit = semester.earnedCredit,
            semesterRank = semester.semesterRank,
            generalRank = semester.generalRank,
        )

        HorizontalDivider(Modifier.padding(horizontal = 16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (lectureDataList.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(20.dp),
                )
            } else {
                lectureDataList.forEach { lecture ->
                    LectureItem(
                        lectureGrade = lecture.lectureGrade,
                        lectureTitle = lecture.title,
                        professor = lecture.professor,
                        credit = lecture.credit,
//                        captureFlag = captureFlag,
                    )
                }
            }
        }
    }
}


//@PreviewLightDark
//@Composable
//private fun SemesterDetailItemPreview() {
//    val tiers = listOf(
//        "A+", "A0", "A-",
//        "B+", "B0", "B-",
//        "C+", "C0", "C-",
//        "D+", "D0", "D-",
//        "P", "F",
//    )
//    SoomsilUSaintTheme {
//        SemesterDetailItem(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState()),
//            semester = Semester(
//                makeSemesterType(2020, "1"),
//                gpa = 4.06.toGrade(),
//                earnedCredit = 17.5.toCredit(),
//                semesterRank = 15,
//                semesterStudentCount = 55,
//                overallRank = 12,
//                overallStudentCount = 100,
//            ),
//            lectureInfos = tiers.map { tier ->
//                LectureInfo(
//                    tier = Tier(tier),
//                    name = "가나다라",
//                    credit = 3.toCredit(),
//                    professorName = tier,
//                )
//            },
//        )
//    }
//}
//
//@PreviewLightDark
//@Composable
//private fun SemesterDetailItemPreview_empty() {
//    SoomsilUSaintTheme {
//        SemesterDetailItem(
//            semester = Semester(
//                makeSemesterType(2020, "1"),
//                gpa = 4.06.toGrade(),
//                earnedCredit = 17.5.toCredit(),
//                semesterRank = 15,
//                semesterStudentCount = 55,
//                overallRank = 12,
//                overallStudentCount = 100,
//            ),
//        )
//    }
//}
