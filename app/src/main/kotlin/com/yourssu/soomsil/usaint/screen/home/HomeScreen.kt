package com.yourssu.soomsil.usaint.screen.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.yourssu.soomsil.usaint.screen.home.components.EmptyChapelCardItem
import com.yourssu.soomsil.usaint.screen.home.components.ReportCardItem
import com.yourssu.soomsil.usaint.screen.home.components.StudentDataItem
import com.yourssu.soomsil.usaint.screen.reportcard.components.LectureItem
import com.yourssu.soomsil.usaint.screen.setting.PasswordChangeDialog
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onReportCardClick: () -> Unit = {},
    onChapelCardClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    HomeScreen(
        hasInitialized = viewModel.hasInitialized,
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.fetchData(refresh = true) },
        homeUiState = homeUiState,
        onProfileClick = onProfileClick,
        onSettingClick = onSettingClick,
        onReportCardClick = {
            viewModel.onCheckReportCardClicked()
            onReportCardClick()
        },
        onChapelCardClick = {
            viewModel.onCheckChapelCardClicked()
            onChapelCardClick()
        },
        onPromotionClicked = { studentData ->
            val intent = Intent(
                Intent.ACTION_VIEW,
                "https://lottery-one.vercel.app?major=${studentData.majors[0]}&name=${studentData.name}&schoolNumber=${studentData.id}".toUri()
            )
            context.startActivity(intent)

        },
        onPasswordChange = viewModel::changePassword,
        snackbarHostState = snackbarHostState,
        onCheckCurrentSemesterClicked = viewModel::onCheckCurrentSemesterClicked,
        onLectureItemClick = viewModel::onCheckLectureItemClicked,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    hasInitialized: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    homeUiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onReportCardClick: () -> Unit = {},
    onChapelCardClick: () -> Unit = {},
    onPromotionClicked: (studentData: StudentData) -> Unit = {},
    onPasswordChange: (password: String) -> Unit = {},
    onCheckCurrentSemesterClicked: () -> Unit = {},
    onLectureItemClick: (String) -> Unit = {},
) {

    val isHomeLoading = homeUiState is HomeUiState.Loading

    var isVisibleGradeBottomSheet by remember { mutableStateOf(false) }
    var isVisiblePasswordChangeDialog by remember { mutableStateOf(false) }

    val gradeBottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = !hasInitialized,
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        Column(
            modifier = Modifier
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
            var showPasswordIncorrectSnackbar by remember {
                if (homeUiState is HomeUiState.Home)
                    homeUiState.showPasswordIncorrectSnackbar
                else
                    mutableStateOf(false)
            }
            var showFailedLoadToStudentDataSnackbar by remember {
                if (homeUiState is HomeUiState.Home)
                    homeUiState.showFailedLoadToStudentDataSnackbar
                else
                    mutableStateOf(false)
            }

            // TODO RUSAINT 고치면 삭제 바람
            val isFailedFetch by remember {
                if (homeUiState is HomeUiState.Home)
                    homeUiState.isFailedFetch
                else
                    mutableStateOf(false)
            }

            if (showFailedLoadToStudentDataSnackbar) {
                LaunchedEffect(Unit) {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(
                            message = "학적 정보를 불러오지 못했어요..",
                            duration = SnackbarDuration.Short
                        )
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            showFailedLoadToStudentDataSnackbar = false
                        }

                        SnackbarResult.Dismissed -> {
                            showFailedLoadToStudentDataSnackbar = false
                        }
                    }
                }
            }

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

            if (isVisibleGradeBottomSheet) {
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
                                    detail = lecture.detail,
                                    onClick = { onLectureItemClick(lecture.title) }
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
            Text(
                text = "내 성적",
                modifier = Modifier.padding(vertical = 4.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

//            ActionTitleItem(
//                title = "\uD83C\uDF81 개강 선물 도착, 복권 뽑으러 가기!",
//                onClick = {
//                    if(studentData != null) {
//                        onPromotionClicked(studentData)
//                    } else {
//                        showFailedLoadToStudentDataSnackbar = true
//                    }
//                },
//            )

//            TODO 나중에 성적시즌이 되면 주석 풀어주세요
            if (studentData?.status == "재학") {
                Spacer(Modifier.height(8.dp))
                ActionTitleItem(
                    title = "이번 학기 성적 확인",
                    onClick = {
                        isVisibleGradeBottomSheet = true
                        onCheckCurrentSemesterClicked()
                    },
                )
            }

            Spacer(Modifier.height(8.dp))
            ReportCardItem(
                reportCardSummary = reportCardSummaryData,
                onReportCardClick = onReportCardClick
            )
            Spacer(Modifier.height(8.dp))
            if(chapelCardData != null) {
                val totalAttendance = chapelCardData.chapelAttendances.size
                val currentAttendance = chapelCardData.chapelAttendances.filter { item ->
                    item.attendance == "출석"
                }.size

                // 과목코드(분반)이 0이면 해당 학기에는 채플 정보가 없는 상태입니다.
                // fetch 과정에서 "No chapel information provided"에러가 발생했으면 0이 저장되어 있습니다
                if (chapelCardData.chapelSimpleData.division != 0L) {
                    ChapelCardItem(
                        onChapelCardClick = onChapelCardClick,
                        chapelData = chapelCardData,
                        currentAttendance = currentAttendance,
                        totalAttendance = totalAttendance,
                    )
                } else {
                    EmptyChapelCardItem(onChapelCardClick = onChapelCardClick)
                }
            } else {
                EmptyChapelCardItem(onChapelCardClick = onChapelCardClick)
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
            hasInitialized = false,
            isRefreshing = false,
            onRefresh = {},
            snackbarHostState = remember { SnackbarHostState() },
            homeUiState = HomeUiState.Home(
                studentData = StudentData.previewData,
                reportCardSummaryData = ReportCardSummaryData.previewData,
                chapelCardData = ChapelData(
                    ChapelSimpleData.previewData,
                    listOf(ChapelAttendanceData.previewData)
                ),
                currentSemesterLectures = null,
                currentSemesterData = null,
                showPasswordIncorrectSnackbar = remember { mutableStateOf(false) },
                showFailedLoadToStudentDataSnackbar = remember { mutableStateOf(false) },
                isFailedFetch = remember { mutableStateOf(false) }
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun HomePreview_RUSAINT_FAILED() {
    // 재학 상태
    SoomsilUSaintTheme {
        HomeScreen(
            hasInitialized = false,
            isRefreshing = false,
            onRefresh = {},
            snackbarHostState = remember { SnackbarHostState() },
            homeUiState = HomeUiState.Home(
                studentData = StudentData.previewData,
                reportCardSummaryData = ReportCardSummaryData.previewData,
                chapelCardData = ChapelData(
                    ChapelSimpleData.previewData,
                    listOf(ChapelAttendanceData.previewData)
                ),
                currentSemesterLectures = null,
                currentSemesterData = null,
                showPasswordIncorrectSnackbar = remember { mutableStateOf(false) },
                showFailedLoadToStudentDataSnackbar = remember { mutableStateOf(false) },
                isFailedFetch = remember { mutableStateOf(true) }
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
            hasInitialized = false,
            isRefreshing = false,
            onRefresh = {},
            snackbarHostState = remember { SnackbarHostState() },
            homeUiState = HomeUiState.Home(
                studentData = StudentData.previewData.copy(status = "휴학"),
                reportCardSummaryData = ReportCardSummaryData.previewData,
                chapelCardData = ChapelData(
                    ChapelSimpleData.previewData,
                    listOf(ChapelAttendanceData.previewData)
                ),
                currentSemesterLectures = null,
                currentSemesterData = null,
                showPasswordIncorrectSnackbar = remember { mutableStateOf(false) },
                showFailedLoadToStudentDataSnackbar = remember { mutableStateOf(false) },
                isFailedFetch = remember { mutableStateOf(false) }
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun HomePreview_loading() {
    SoomsilUSaintTheme {
        HomeScreen(
            hasInitialized = false,
            isRefreshing = false,
            onRefresh = {},
            snackbarHostState = remember { SnackbarHostState() },
            homeUiState = HomeUiState.Loading,
        )
    }
}
