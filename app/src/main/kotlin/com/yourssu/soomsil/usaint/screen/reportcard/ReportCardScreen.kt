package com.yourssu.soomsil.usaint.screen.reportcard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.yourssu.soomsil.usaint.core.model.Pass
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.screen.home.components.ReportOutline
import com.yourssu.soomsil.usaint.screen.reportcard.components.GradeSummary
import com.yourssu.soomsil.usaint.screen.reportcard.components.LectureItem
import com.yourssu.soomsil.usaint.ui.components.Chart
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun ReportCardScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportCardViewModel = hiltViewModel(),
) {
    val reportCardUiState by viewModel.reportCardUiState.collectAsStateWithLifecycle()

    ReportCardScreen(
        isFetching = viewModel.isFetching,
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.fetchData(refresh = true) },
        reportCardUiState = reportCardUiState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportCardScreen(
    isFetching: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
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
                    visible = isFetching,
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
            ) {
                if (isReportCardLoading) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    return@Column
                }

                ChartSummary(reportCardUiState)
                SemesterTabsAndDetail(reportCardUiState)
            }
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
                        modifier = Modifier
                            .height(200.dp)
                            .padding(horizontal = 20.dp),
                    )
                }
                Spacer(Modifier.height(16.dp))
                ReportOutline(
                    title = "평균학점",
                    actualValue = summary.gradePointsAverage.toString(),
                    maxValue = "4.50",
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
private fun SemesterTabsAndDetail(
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
                        val sortedLectureDataList =
                            lectureDataList.sortedBy { it.lectureScore }.reversed()

                        Column {
                            GradeSummary(
                                gpa = semester.gradePointsAverage,
                                earnedCredit = semester.earnedCredit,
                                semesterRank = semester.semesterRank,
                                generalRank = semester.generalRank,
                            )

                            HorizontalDivider(Modifier.padding(horizontal = 12.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                sortedLectureDataList.forEach { lecture ->
                                    LectureItem(
                                        lectureGrade = lecture.lectureGrade,
                                        lectureTitle = lecture.title,
                                        professor = lecture.professor,
                                        credit = lecture.credit,
                                    )
                                }
                            }
                        }
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
    val lectureGrades = listOf(
        "A+", "A0", "A-",
        "B+", "B0", "B-",
        "C+", "C0", "C-",
        "D+", "D0", "D-",
        "P", "F", "Unknown"
    ).map { LectureGrade.from(it) }
    val lectures = listOf(
        lectureGrades.map { makePreviewLectureData(2022, SemesterType.One, it) },
        lectureGrades.map { makePreviewLectureData(2022, SemesterType.Two, it) },
        lectureGrades.map { makePreviewLectureData(2023, SemesterType.One, it) },
        lectureGrades.map { makePreviewLectureData(2023, SemesterType.Summer, it) },
    )

    SoomsilUSaintTheme {
        ReportCardScreen(
            isFetching = false,
            isRefreshing = false,
            onRefresh = {},
            reportCardUiState = ReportCardUiState.ReportCard(
                summary = ReportCardSummaryData.previewData,
                semesterWithLectures = semesters.zip(lectures).toMap(),
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun ReportCardScreenPreview_loading() {
    SoomsilUSaintTheme {
        ReportCardScreen(
            isFetching = false,
            isRefreshing = false,
            onRefresh = {},
            reportCardUiState = ReportCardUiState.Loading,
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

private fun makePreviewLectureData(
    year: Int,
    semester: SemesterType,
    lectureGrade: LectureGrade,
) = LectureData(
    year = year,
    semester = semester,
    code = "1234",
    title = "가나다",
    credit = 3f,
    lectureGrade = lectureGrade,
    lectureScore = Pass,
    professor = "professor",
)
