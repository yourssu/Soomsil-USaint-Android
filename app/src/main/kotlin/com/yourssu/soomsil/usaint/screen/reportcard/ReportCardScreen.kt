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
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.LectureGrade
import com.yourssu.soomsil.usaint.core.model.Pass
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.screen.home.components.ReportOutline
import com.yourssu.soomsil.usaint.screen.reportcard.components.GradeSummary
import com.yourssu.soomsil.usaint.screen.reportcard.components.LectureItem
import com.yourssu.soomsil.usaint.screen.setting.PasswordChangeDialog
import com.yourssu.soomsil.usaint.ui.components.Chart
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import kotlinx.coroutines.launch

@Composable
fun ReportCardScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: ReportCardViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val reportCardUiState by viewModel.reportCardUiState.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.reportCardEventFlow.collect { uiEvent ->
                when (uiEvent) {
                    ReportCardUiEvent.FetchStart ->
                        snackbarHostState.showSnackbar("성적 정보를 불러오고 있습니다.")

                    ReportCardUiEvent.FetchSuccess ->
                        snackbarHostState.showSnackbar("성적 정보를 불러왔습니다.")

                    is ReportCardUiEvent.FetchFailed ->
                        snackbarHostState.showSnackbar(uiEvent.message?.let { "에러 발생: $it" }
                            ?: "문제가 발생했습니다.")
                }
            }
        }
    }

    ReportCardScreen(
        hasInitialized = viewModel.hasInitialized,
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.fetchData(refresh = true) },
        reportCardUiState = reportCardUiState,
        onPasswordChange = viewModel::changePassword,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onLectureItemClick = viewModel::onCheckLectureItemClicked,
        onSemesterItemClick = viewModel::onCheckSemesterItemClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportCardScreen(
    hasInitialized: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onPasswordChange: (password: String) -> Unit,
    reportCardUiState: ReportCardUiState,
    modifier: Modifier = Modifier,
    onLectureItemClick: (String) -> Unit = {},
    onSemesterItemClick: (SemesterData) -> Unit = {},
) {
    val isReportCardLoading = reportCardUiState is ReportCardUiState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = !hasInitialized,
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            LinearProgressIndicator(Modifier.fillMaxWidth().semantics {
                contentDescription = "선형로딩" // baseline-Profile
            })
        }
        Column(
            Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
        ) {
            if (isReportCardLoading) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .semantics {
                            contentDescription = "로딩" // baseline-Profile에서 감지하기 위한 desc
                        },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            var showPasswordIncorrectSnackbar by remember {
                if (reportCardUiState is ReportCardUiState.ReportCard)
                    reportCardUiState.showPasswordIncorrectSnackbar
                else
                    mutableStateOf(false)
            }

            var isVisiblePasswordChangeDialog by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            if (showPasswordIncorrectSnackbar) {
                LaunchedEffect(Unit) {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState
                        .showSnackbar(
                            message = "유세인트 로그인에 실패했습니다.",
                            actionLabel = "비밀번호 변경",
                            // Defaults to SnackbarDuration.Short
                            duration = SnackbarDuration.Indefinite
                        )
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            showPasswordIncorrectSnackbar = false
                            isVisiblePasswordChangeDialog = true
                        }

                        SnackbarResult.Dismissed -> {
                            showPasswordIncorrectSnackbar = false
                        }
                    }
                }
            }

            if (isVisiblePasswordChangeDialog) {
                snackbarHostState.currentSnackbarData?.dismiss()
                PasswordChangeDialog(
                    onDismissRequest = {
                        showPasswordIncorrectSnackbar = true
                        isVisiblePasswordChangeDialog = false
                    },
                    onConfirmClick = {
                        isVisiblePasswordChangeDialog = false
                        showPasswordIncorrectSnackbar = false
                        onPasswordChange(it)
                        snackbarHostState.currentSnackbarData?.dismiss()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "앞으로 해당 비밀번호를 사용할게요. 정보를 다시 불러옵니다.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }
            ChartSummary(reportCardUiState)
            SemesterTabsAndDetail(
                reportCardUiState = reportCardUiState,
                onLectureItemClick = onLectureItemClick,
                onSemesterItemClick = onSemesterItemClick,
            )
        }
    }
}

@Composable
private fun ChartSummary(
    reportCardUiState: ReportCardUiState,
    modifier: Modifier = Modifier,
) {
    when (reportCardUiState) {
        is ReportCardUiState.Loading -> Unit

        is ReportCardUiState.ReportCard -> {
            Column(modifier) {
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
    onLectureItemClick: (String) -> Unit = {},
    onSemesterItemClick: (SemesterData) -> Unit = {},
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
                                onClick = {
                                    selectedTabIndex = index
                                    onSemesterItemClick(semester)
                                },
                                text = {
                                    Text(text = "${semester.year % 100}년 ${semester.semester.kor}학기")
                                }
                            )
                        }
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    verticalAlignment = Alignment.Top,
                ) { pagerIndex ->
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
                                        detail = lecture.detail,
                                        onClick = { onLectureItemClick(lecture.title) }
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
            hasInitialized = false,
            isRefreshing = false,
            onRefresh = {},
            onPasswordChange = {},
            snackbarHostState = remember { SnackbarHostState() },
            reportCardUiState = ReportCardUiState.ReportCard(
                summary = ReportCardSummaryData.previewData,
                semesterWithLectures = semesters.zip(lectures).toMap(),
                showPasswordIncorrectSnackbar = remember { mutableStateOf(false) }
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun ReportCardScreenPreview_loading() {
    SoomsilUSaintTheme {
        ReportCardScreen(
            hasInitialized = false,
            isRefreshing = false,
            onRefresh = {},
            snackbarHostState = remember { SnackbarHostState() },
            onPasswordChange = {},
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
