package com.yourssu.soomsil.usaint.screen.reportcard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.LectureGrade
import com.yourssu.soomsil.usaint.core.model.LectureScore
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.screen.home.components.ReportOutline
import com.yourssu.soomsil.usaint.ui.component.Chart
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun ReportCardScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportCardViewModel = hiltViewModel(),
) {
    val reportCardUiState by viewModel.reportCardUiState.collectAsStateWithLifecycle()

    ReportCardScreen(
        reportCardUiState = reportCardUiState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportCardScreen(
    reportCardUiState: ReportCardUiState,
    modifier: Modifier = Modifier,
) {
    val isReportCardLoading = reportCardUiState is ReportCardUiState.Loading

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = "성적") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            ChartSummary(
                reportCardUiState,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}

@Composable
private fun ChartSummary(
    reportCardUiState: ReportCardUiState,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        when (reportCardUiState) {
            is ReportCardUiState.Loading -> Unit
            is ReportCardUiState.ReportCard -> {
                val semesters = reportCardUiState.semesterWithLectures.keys.toList()
                val summary = reportCardUiState.summary

                if (semesters.isNotEmpty()) {
                    Chart(
                        semesters = semesters,
                        modifier = Modifier.height(200.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                ReportOutline(
                    title = "평균학점",
                    actualValue = summary.gradePointsAverage.toString(),
                    maxValue = 4.5f.toString(),
                )
                ReportOutline(
                    title = "취득학점",
                    actualValue = summary.earnedCredits.toString(),
                    maxValue = summary.graduationPoints.toString(),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ReportCardScreenPreview() {
    val semesters = listOf(
        makePreviewSemesterData(2022, SemesterType.One, 3.5f),
        makePreviewSemesterData(2022, SemesterType.Two, 3.7f),
        makePreviewSemesterData(2023, SemesterType.One, 4.2f),
        makePreviewSemesterData(2023, SemesterType.Summer, 4.5f),
    )
    val lectures = listOf(
        makePreviewLectureDataList(2022, SemesterType.One),
        makePreviewLectureDataList(2022, SemesterType.Two),
        makePreviewLectureDataList(2023, SemesterType.One),
        makePreviewLectureDataList(2023, SemesterType.Summer),
    )

    SoomsilUSaintTheme {
        ReportCardScreen(
            reportCardUiState = ReportCardUiState.ReportCard(
                summary = ReportCardSummaryData.previewData,
                semesterWithLectures = semesters.zip(lectures).toMap(),
            )
        )
    }
}

private fun makePreviewSemesterData(
    year: Int,
    semester: SemesterType,
    grade: Float,
) = SemesterData(
    year = year,
    semester = semester,
    gradePointsAverage = grade,
    attemptedCredit = 0f,
    earnedCredit = 0f,
    pfEarnedCredit = 0f,
    semesterRank = 0 to 0,
    generalRank = 0 to 0,
)

private fun makePreviewLectureDataList(
    year: Int,
    semester: SemesterType,
) = listOf(
    LectureData(
        year = year,
        semester = semester,
        code = "1234",
        title = "가나다",
        credit = 3f,
        lectureGrade = LectureGrade.from("A+"),
        lectureScore = LectureScore.from("90"),
        professor = "professor",
    ),
    LectureData(
        year = year,
        semester = semester,
        code = "5678",
        title = "라마바",
        credit = 2f,
        lectureGrade = LectureGrade.from("P"),
        lectureScore = LectureScore.from("P"),
        professor = "professor",
    ),
)
