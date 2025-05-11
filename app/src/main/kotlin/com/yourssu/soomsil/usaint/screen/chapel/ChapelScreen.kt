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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.screen.chapel.components.ChapelAttendanceItem
import com.yourssu.soomsil.usaint.screen.chapel.components.ChapelSummary

@Composable
fun ChapelScreen(
    modifier: Modifier = Modifier,
    viewModel: ChapelViewModel = hiltViewModel(),
) {
    val chapelCardUiState by viewModel.chapelCardUiState.collectAsStateWithLifecycle()

    ChapelScreen(
        modifier = modifier,
        chapelCardUiState = chapelCardUiState,
        isRefreshing = viewModel.isRefreshing,
        isFetching = viewModel.isFetching,
        onRefresh = { viewModel.fetchData(refresh = true) }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapelScreen(
    modifier: Modifier = Modifier,
    chapelCardUiState: ChapelUiState,
    isFetching: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    val isChapelCardLoading = chapelCardUiState is ChapelUiState.Loading

    Scaffold(
        modifier = modifier,
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
                    SemesterTabsAndDetail(chapelCardUiState)
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

        is ChapelUiState.ChapelCard -> {
            val semesterWithChapelMap = chapelUiState.semesterWithChapel
                .mapValues { (_, data) ->
                    chapelUiState.chapelWithAttendance.find {
                        data.division == it.chapelSimpleData.division
                    }
                }
            val semesters = semesterWithChapelMap.keys.toList()
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

                    semesterWithChapelMap[semester]?.let { chapelData ->
                        val attendances = chapelData.chapelAttendances
                        Column {
                            ChapelSummary(
                                chapelSimpleData = chapelData.chapelSimpleData,
                                currentAttendance = chapelData.chapelSimpleData.currentAttendance,
                                totalAttendance = chapelData.chapelSimpleData.totalAttendance
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
                                    text = "${chapelData.chapelSimpleData.absenceTime}회 결석",
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
}