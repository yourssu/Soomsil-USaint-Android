package com.yourssu.soomsil.usaint.screen.chapel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yourssu.soomsil.usaint.R

@Composable
fun ChapelScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: ChapelViewModel = hiltViewModel(),
) {
//    val chapelUiState by viewModel.chapelUiState.collectAsStateWithLifecycle()
//
//    ChapelScreen(
//        snackbarHostState = snackbarHostState,
//        modifier = modifier,
//        chapelUiState = chapelUiState,
//        isRefreshing = viewModel.isRefreshing,
//        hasInitialized = viewModel.hasInitialized,
//        onRefresh = { viewModel.fetchData(refresh = true) },
//        onPasswordChange = viewModel::changePassword
//    )
    val isOnLeave by viewModel.isOnLeave.collectAsStateWithLifecycle()

    ChapelSeatScreen(
        isOnLeave = isOnLeave,
        modifier = modifier
    )

}
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapelScreen(
    chapelUiState: ChapelUiState,
    snackbarHostState: SnackbarHostState,
    hasInitialized: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    onPasswordChange: (password: String) -> Unit,
) {
    val isChapelLoading = chapelUiState is ChapelUiState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
    ) {
        Column {
            AnimatedVisibility(
                visible = !hasInitialized,
//                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            Column(
                Modifier.verticalScroll(rememberScrollState()),
            ) {

                if (isChapelLoading) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    var showPasswordIncorrectSnackbar by remember {
                        if (chapelUiState is ChapelUiState.Chapel) {
                            chapelUiState.showPasswordIncorrectSnackbar
                        } else {
                            mutableStateOf(false)
                        }
                    }

                    val scope = rememberCoroutineScope()
                    var isVisiblePasswordChangeDialog by remember { mutableStateOf(false) }

                    if (showPasswordIncorrectSnackbar) {
                        LaunchedEffect(Unit) {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            val result = snackbarHostState.showSnackbar(
                                message = "유세인트 로그인에 실패했습니다.",
                                actionLabel = "비밀번호 변경",
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

                    SemesterTabsAndDetail(chapelUiState)
                }

            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SemesterTabsAndDetail(
    chapelUiState: ChapelUiState,
    modifier: Modifier = Modifier,
) {
    when (chapelUiState) {
        is ChapelUiState.Loading -> Unit

        is ChapelUiState.Chapel -> {

            val chapels = chapelUiState.chapels
                .sortedWith(
                    compareBy(
                        { it.chapelSimpleData.year },
                        { it.chapelSimpleData.semester })
                )
                .reversed() // 연도와 학기에 맞춰 정렬 (현재->과거 순)
            val pagerState = rememberPagerState { chapels.size }
            var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

            LaunchedEffect(selectedTabIndex) {
                pagerState.animateScrollToPage(selectedTabIndex)
            }
            LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
                if (!pagerState.isScrollInProgress)
                    selectedTabIndex = pagerState.currentPage
            }

            Column(modifier) {
                if (chapels.isNotEmpty()) {
                    SecondaryScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage
                    ) {


                        chapels.forEachIndexed { index, chapelData ->
                            // 현재학기를 과거학기로 잡았을 경우 현재학기 탭에만 뜨도록
                            if (chapelUiState.chapelCard?.chapelSimpleData?.year == chapelData.chapelSimpleData.year &&
                                chapelUiState.chapelCard.chapelSimpleData.semester == chapelData.chapelSimpleData.semester
                            )
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = { selectedTabIndex = index },
                                    text = {
                                        Text(text = "현재 학기")
                                    }
                                )
                            else
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = { selectedTabIndex = index },
                                    text = {
                                        Text(text = "${chapelData.chapelSimpleData.year % 100}년 ${chapelData.chapelSimpleData.semester.kor}학기")
                                    }
                                )
                        }
                    }
                }

                HorizontalPager(state = pagerState) { pagerIndex ->
                    val chapelPagerData = chapels.getOrNull(pagerIndex) ?: return@HorizontalPager
                    val attendances = chapelPagerData.chapelAttendances
                    val totalAttendance = attendances.size
                    val currentAttendance by remember {
                        mutableIntStateOf(
                            attendances.filter {
                                it.attendance == "출석"
                            }.size
                        )
                    }
                    Column {
                        ChapelSummary(
                            chapelSimpleData = chapelPagerData.chapelSimpleData,
                            currentAttendance = currentAttendance,
                            totalAttendance = totalAttendance
                        )

                        HorizontalDivider(Modifier.padding(horizontal = 12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)

                        ) {
                            Text(
                                text = "출결 현황",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )

                            Text(
                                text = "${chapelPagerData.chapelSimpleData.absenceTime}회 결석",
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Normal
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            attendances.forEach { attendance ->
                                ChapelAttendanceItem(
                                    attendanceData = attendance
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}
*/
// --- New Chapel Screen ---

@Composable
fun ChapelHeader(
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "채플",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0A0A0A),
            letterSpacing = (-0.5).sp
        )
    }
}

// ─── Hero Card ───

@Composable
fun SeatHeroCard(
    seatNumber: String,
    floor: String,
    zone: String,
    onViewSeatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0062FF), RoundedCornerShape(20.dp))
            .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "내 자리",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xCCFFFFFF)
        )
        Text(
            text = seatNumber,
            fontSize = 72.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = (-3).sp,
            lineHeight = 72.sp
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0x1FFFFFFF)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$floor · $zone",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xCCFFFFFF)
            )
            Text(
                text = "좌석 위치 보기 →",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.clickable { onViewSeatClick() }
            )
        }
    }
}

// ─── Attendance Gauge ───

// 한 학기 채플 이수 횟수는 최대 7회
private const val MAX_REQUIRED_CHAPEL = 7

@Composable
fun AttendanceGauge(
    attended: Int,
    total: Int,
    late: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val cappedTotal = total.coerceAtMost(MAX_REQUIRED_CHAPEL)
    val remaining = (cappedTotal - attended).coerceAtLeast(0)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(14.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 상단: 라벨 + 횟수
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "이번 학기 남은 출석",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0A0A0A),
                letterSpacing = (-0.2).sp
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "$remaining",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0062FF),
                    letterSpacing = (-0.4).sp
                )
                Text(
                    text = "/ ${cappedTotal}회",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF9CA3AF)
                )
            }
        }

        // 프로그레스 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFFF1F5F9), RoundedCornerShape(9999.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(Color(0xFF0062FF), RoundedCornerShape(9999.dp))
            )
        }

        // 하단: 출석 현황 + 퍼센트
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${attended}회 출석 · ${late}회 지각",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9CA3AF)
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0062FF)
            )
        }
    }
}

// ─── CTA Button ───

@Composable
fun AttendanceCta(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A0A0A)
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_qr_code),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
                Text(
                    text = "출석 인증하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.2).sp
                )
            }
        }
        Text(
            text = "입실 후 좌석 QR을 스캔해주세요",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9CA3AF)
        )
    }
}

// ─── Screen ───

@Composable
@Preview
fun ChapelSeatScreen(
    seatNumber: String = "B-12",
    seatFloor: String = "1층 앞자리",
    seatZone: String = "A구역",
    attended: Int = 5,
    total: Int = 7,
    late: Int = 1,
    progress: Float = 0.71f,
    onBackClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onViewSeatClick: () -> Unit = {},
    onAttendClick: () -> Unit = {},
    isOnLeave: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ChapelHeader(
            onBackClick = onBackClick,
            onInfoClick = onInfoClick
        )

        if (isOnLeave) {
            ChapelOnLeaveMessage(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SeatHeroCard(
                    seatNumber = seatNumber,
                    floor = seatFloor,
                    zone = seatZone,
                    onViewSeatClick = onViewSeatClick
                )
                AttendanceGauge(
                    attended = attended,
                    total = total,
                    late = late,
                    progress = progress
                )
            }

            // TODO: 출석 인증(QR) 기능 구현 전까지 임시 비활성화
            // AttendanceCta(onClick = onAttendClick)
        }
    }
}

// ─── 휴학 안내 ───

@Composable
private fun ChapelOnLeaveMessage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "휴학 중이에요",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0A0A0A),
            letterSpacing = (-0.3).sp
        )
        Text(
            text = "휴학 중이라 수강할 채플이 없어요",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9CA3AF),
            letterSpacing = (-0.2).sp
        )
    }
}
