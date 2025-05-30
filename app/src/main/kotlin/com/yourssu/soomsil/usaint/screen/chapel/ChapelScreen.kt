package com.yourssu.soomsil.usaint.screen.chapel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.screen.chapel.components.ChapelAttendanceItem
import com.yourssu.soomsil.usaint.screen.chapel.components.ChapelSummary
import com.yourssu.soomsil.usaint.screen.setting.PasswordChangeDialog
import kotlinx.coroutines.launch

@Composable
fun ChapelScreen(
    modifier: Modifier = Modifier,
    viewModel: ChapelViewModel = hiltViewModel(),
) {
    val chapelUiState by viewModel.chapelUiState.collectAsStateWithLifecycle()

    ChapelScreen(
        modifier = modifier,
        chapelUiState = chapelUiState,
        isRefreshing = viewModel.isRefreshing,
        isFetching = viewModel.isFetching,
        onRefresh = { viewModel.fetchData(refresh = true) },
        onPasswordChange = viewModel::changePassword
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapelScreen(
    modifier: Modifier = Modifier,
    chapelUiState: ChapelUiState,
    isFetching: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onPasswordChange: (password: String) -> Unit,
) {
    val isChapelCardLoading = chapelUiState is ChapelUiState.Loading

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isVisiblePasswordChangeDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            Box {
                TopAppBar(title = { Text(text = "채플") })
                AnimatedVisibility(
                    visible = isFetching || isChapelCardLoading,
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

            var showPasswordIncorrectSnackbar by remember {
                if (chapelUiState is ChapelUiState.Chapel)
                    chapelUiState.showPasswordIncorrectSnackbar
                else
                    mutableStateOf(false)
            }

            if(showPasswordIncorrectSnackbar) {
                snackbarHostState.currentSnackbarData?.dismiss()
                scope.launch {
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

            if(isVisiblePasswordChangeDialog) {
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

            Column(
                Modifier.verticalScroll(rememberScrollState()),
            ) {
                if(isChapelCardLoading) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
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
                .sortedWith(compareBy({ it.chapelSimpleData.year }, { it.chapelSimpleData.semester }))
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
                    SecondaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {


                        chapels.forEachIndexed { index, chapelData ->
                            // 현재학기를 과거학기로 잡았을 경우 현재학기 탭에만 뜨도록
                            if(chapelUiState.chapelCard?.chapelSimpleData?.year == chapelData.chapelSimpleData.year &&
                                chapelUiState.chapelCard.chapelSimpleData.semester == chapelData.chapelSimpleData.semester)
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
                                    onClick = { selectedTabIndex = index},
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
                            }.size)
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