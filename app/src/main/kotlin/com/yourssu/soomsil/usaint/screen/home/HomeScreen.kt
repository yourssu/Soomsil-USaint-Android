package com.yourssu.soomsil.usaint.screen.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.core.model.ChapelAttendanceData
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.model.ChapelSimpleData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.screen.home.components.ActionTitleItem
import com.yourssu.soomsil.usaint.screen.home.components.ChapelCardItem
import com.yourssu.soomsil.usaint.screen.home.components.ReportCardItem
import com.yourssu.soomsil.usaint.screen.home.components.StudentDataItem
import com.yourssu.soomsil.usaint.screen.reportcard.components.LectureItem
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onReportCardClick: () -> Unit = {},
    onChapelCardClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    HomeScreen(
        isFetching = viewModel.isFetching,
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.fetchData(refresh = true) },
        homeUiState = homeUiState,
        onProfileClick = onProfileClick,
        onSettingClick = onSettingClick,
        onReportCardClick = onReportCardClick,
        onChapelCardClick = onChapelCardClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    isFetching: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    homeUiState: HomeUiState,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onReportCardClick: () -> Unit = {},
    onChapelCardClick: () -> Unit = {},
) {
    // 임시
    val context = LocalContext.current

    val isHomeLoading = homeUiState is HomeUiState.Loading

    var isVisibleGradeBottomSheet by remember { mutableStateOf(false) }

    val gradeBottomSheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier,
        topBar = {
            Box {
                TopAppBar(title = { Text(text = "유세인트") })
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
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp,
                    ),
            ) {
                if (isHomeLoading) {
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

                val studentData =
                    if (homeUiState is HomeUiState.Home) homeUiState.studentData else null
                val reportCardSummaryData =
                    if (homeUiState is HomeUiState.Home) homeUiState.reportCardSummaryData else null
                val chapelCardData =
                    if (homeUiState is HomeUiState.Home) homeUiState.chapelCardData else null
                val currentSemesterLectureData =
                    if (homeUiState is HomeUiState.Home) homeUiState.currentSemesterLectures else null
                val currentSemesterData =
                    if (homeUiState is HomeUiState.Home) homeUiState.currentSemesterData else null

                if(isVisibleGradeBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { isVisibleGradeBottomSheet = false },
                        sheetState = gradeBottomSheetState,
                        modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
                    ) {
                        if (currentSemesterData != null) {
                            Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                                Text(text = "${currentSemesterData.year}년 ${currentSemesterData.semester.kor}학기")
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                ) {
                                    Text(
                                        text = String.format(
                                            "%.2f",
                                            currentSemesterData.gradePointsAverage
                                        ),
                                        style = MaterialTheme.typography.displaySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = " / 4.50",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            HorizontalDivider(Modifier.padding(horizontal = 12.dp))
                        }


                        if (currentSemesterLectureData != null) {
                            // 바텀시트의 최대 높이를 넘어가는 데이터들에 대한 스크롤링을 위해서는 LazyColumn 사용
                            LazyColumn {
                                items(currentSemesterLectureData) { lecture ->
                                    LectureItem(
                                        lectureGrade = lecture.lectureGrade,
                                        lectureTitle = lecture.title,
                                        professor = lecture.professor,
                                        credit = lecture.credit,
                                    )
                                }
                            }
                        } else {
                            Text(
                                modifier = Modifier.padding(12.dp),
                                text = "현재 학기 성적 데이터가 없어요."
                            )
                        }
                    }
                }

                StudentDataItem(
                    studentData = studentData,
                    onProfileClick = onProfileClick,
                    onSettingClick = onSettingClick,
                )

                Spacer(Modifier.height(8.dp))
                ElevatedCard(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://trendwave-one.vercel.app/".toUri()
                        )
                        context.startActivity(intent)
                    }
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "\"TREND WAVE 2025\" 티켓 받으러 가기",
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 16.dp)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "내 성적",
                    modifier = Modifier.padding(vertical = 4.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                if (studentData?.status == "재학") {
                    Spacer(Modifier.height(8.dp))
                    ActionTitleItem(
                        title = "이번 학기 성적 확인",
                        onClick = {
                            isVisibleGradeBottomSheet = true
//                            Toast.makeText(context, "서비스 예정입니다.", Toast.LENGTH_SHORT).show()
                        },
                    )
                }

                Spacer(Modifier.height(8.dp))
                ReportCardItem(
                    reportCardSummary = reportCardSummaryData,
                    onReportCardClick = onReportCardClick,
                )


                chapelCardData?.let {
                    Spacer(Modifier.height(8.dp))

                    val totalAttendance = it.chapelAttendances.size
                    val currentAttendance = it.chapelAttendances.filter { item ->
                        item.attendance == "출석"
                    }.size

                    // 앱 최초 실행 후 현재 학기에 채플 정보 없으면 카드 띄우지 않음
                    // 단, 채플 카드가 뜬 적이 있다면 채플 정보가 없는 학기로 수정해서 새로고침해도 기존 카드로 유지됨
                    if(it.chapelSimpleData.division != 0L)
                        ChapelCardItem(
                            onChapelCardClick = onChapelCardClick,
                            chapelData = chapelCardData,
                            currentAttendance = currentAttendance,
                            totalAttendance = totalAttendance,
                        )
                }

            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomePreview_being() {
    // 재학 상태
    SoomsilUSaintTheme {
        HomeScreen(
            isFetching = false,
            isRefreshing = false,
            onRefresh = {},
            homeUiState = HomeUiState.Home(
                studentData = StudentData.previewData,
                reportCardSummaryData = ReportCardSummaryData.previewData,
                chapelCardData = ChapelData(ChapelSimpleData.previewData, listOf(ChapelAttendanceData.previewData)),
                currentSemesterLectures = null,
                currentSemesterData = null,
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun HomePreview_leave() {
    // 휴학 상태
    SoomsilUSaintTheme {
        HomeScreen(
            isFetching = false,
            isRefreshing = false,
            onRefresh = {},
            homeUiState = HomeUiState.Home(
                studentData = StudentData.previewData.copy(status = "휴학"),
                reportCardSummaryData = ReportCardSummaryData.previewData,
                chapelCardData = ChapelData(ChapelSimpleData.previewData, listOf(ChapelAttendanceData.previewData)),
                currentSemesterLectures = null,
                currentSemesterData = null,
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun HomePreview_loading() {
    SoomsilUSaintTheme {
        HomeScreen(
            isFetching = false,
            isRefreshing = false,
            onRefresh = {},
            homeUiState = HomeUiState.Loading,
        )
    }
}
