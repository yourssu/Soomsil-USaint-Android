package com.yourssu.soomsil.usaint.screen.reportcard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.yourssu.soomsil.usaint.screen.reportcard.components.SemesterDetailItem
import com.yourssu.soomsil.usaint.ui.component.Chart
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun ReportCardScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportCardViewModel = hiltViewModel(),
) {
    val reportCardUiState by viewModel.reportCardUiState.collectAsStateWithLifecycle()

    ReportCardScreen(
        isFetching = viewModel.isFetching,
        reportCardUiState = reportCardUiState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportCardScreen(
    isFetching: Boolean,
    reportCardUiState: ReportCardUiState,
    modifier: Modifier = Modifier,
) {
    val isReportCardLoading = reportCardUiState is ReportCardUiState.Loading

    Scaffold(
        modifier = modifier,
        topBar = {
            Box {
                TopAppBar(title = { Text(text = "성적") })
                AnimatedVisibility(
                    visible = isFetching || isReportCardLoading,
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }
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
                    .sortedWith(compareBy({ it.year }, { it.semester }))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SemesterDetail(
    reportCardUiState: ReportCardUiState,
    modifier: Modifier = Modifier,
) {
    when (reportCardUiState) {
        is ReportCardUiState.Loading -> Unit

        is ReportCardUiState.ReportCard -> {
            val semesterWithLecturesMap = reportCardUiState.semesterWithLectures
            val semesters = semesterWithLecturesMap.keys.toList()
                .sortedWith(compareBy({ it.year }, { it.semester }))
            val pagerState = rememberPagerState { semesters.size }
            var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

            LaunchedEffect(selectedTabIndex) {
                pagerState.animateScrollToPage(selectedTabIndex)
            }
            LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
                if (!pagerState.isScrollInProgress)
                    selectedTabIndex = pagerState.currentPage
            }

            Column(modifier) {
                if (semesters.isNotEmpty()) {
                    SecondaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
                        semesters.forEachIndexed { index, semester ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(text = "${semester.year % 100}년 ${semester.semester.kor}학기")
                                }
                            )
                        }
                    }
                }

                HorizontalPager(state = pagerState) { pagerIndex ->
                    val semester = semesters.getOrNull(pagerIndex) ?: return@HorizontalPager
                    semesterWithLecturesMap[semester]?.let { lectureDataList ->
//                        SemesterDetailItem(
//
//                        )
                    }
                }
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
            isFetching = false,
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
