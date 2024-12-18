package com.yourssu.soomsil.usaint.screen.semesterdetail

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.ListItem
import com.yourssu.design.system.compose.atom.TopBarButton
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.design.system.compose.component.ScrollableTabBar
import com.yourssu.design.system.compose.component.Tab
import com.yourssu.design.system.compose.component.topbar.TopBar
import com.yourssu.soomsil.usaint.ui.entities.LectureInfo
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.Tier
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.util.Capturable
import com.yourssu.soomsil.usaint.util.CaptureController
import com.yourssu.soomsil.usaint.util.PullToRefreshColumn
import com.yourssu.soomsil.usaint.util.rememberCaptureController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.yourssu.design.R as YdsR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SemesterDetailScreen(
    isRefreshing: Boolean,
    onRefresh: (String) -> Unit,
    initialPage: Int,
    semesters: List<Semester>,
    semesterCoursesMap: Map<String, List<LectureInfo>>,
    captureController: CaptureController,
    captureFlag: CaptureFlag,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCaptureFlagChanged: (CaptureFlag) -> Unit = {},
    onCaptured: (semesterName: String, Bitmap) -> Unit = { _, _ -> },
) {
    val pagerState = rememberPagerState { semesters.size }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(initialPage) {
        pagerState.scrollToPage(initialPage)
    }

    YdsScaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopBar(
                    navigationIcon = {
                        TopBarButton(
                            onClick = onBackClick,
                            icon = YdsR.drawable.ic_arrow_left_line,
                        )
                    },
                ) {
                    TopBarButton(
                        onClick = {
                            coroutineScope.launch { showBottomSheet = true }
                        },
                        icon = YdsR.drawable.ic_camera_line,
                    )
                    TopBarButton(
                        onClick = {
                            onRefresh(semesters[pagerState.currentPage].fullName)
                        },
                        icon = YdsR.drawable.ic_refresh_line,
                    )
                }
                if (semesters.isNotEmpty()) {
                    ScrollableTabBar(selectedTabIndex = pagerState.currentPage) {
                        semesters.forEachIndexed { i, semester ->
                            Tab(
                                selected = pagerState.currentPage == i,
                                onClick = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(i) }
                                },
                                text = semester.fullName.substring(2),
                            )
                        }
                    }
                }
            }
        },
    ) {
        PullToRefreshColumn(
            isRefreshing = isRefreshing,
            onRefresh = {
                onRefresh(semesters[pagerState.currentPage].fullName)
            }
        ) {
            HorizontalPager(state = pagerState) { pagerIdx ->
                val semester = semesters[pagerIdx]
                semesterCoursesMap[semester.fullName]?.let { courses ->
                    Capturable(
                        controller = captureController,
                        predicate = { pagerState.currentPage == pagerIdx },
                        onCaptured = { bitmap -> onCaptured(semester.fullName, bitmap) },
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .wrapContentHeight(unbounded = true), // 기기 밖의 화면도 캡처하기 위해 필요함
                    ) {
                        SemesterDetailItem(
                            semester = semester,
                            lectureInfos = courses,
                            modifier = Modifier.fillMaxSize(),
                            captureFlag = captureFlag,
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false },
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = YdsTheme.colors.bgNormal,
            scrimColor = YdsTheme.colors.dimNormal,
        ) {
            Column(
                Modifier
                    .heightIn(
                        min = 88.dp,
                        max = LocalConfiguration.current.screenHeightDp.dp - 88.dp,
                    )
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
            ) {
                ListItem(
                    text = "원본으로 저장",
                    onClick = {
                        onCaptureFlagChanged(CaptureFlag.Original)
                        captureController.capture() // capture 이벤트 요청
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
                    },
                )
                ListItem(
                    text = "강의정보 가리고 저장",
                    onClick = {
                        onCaptureFlagChanged(CaptureFlag.HidingInfo)
                        captureController.capture() // capture 이벤트 요청
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
                    },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SemesterDetailScreenPreview() {
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    YdsTheme {
        SemesterDetailScreen(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    delay(1000)
                    isRefreshing = false
                }
            },
            initialPage = 0,
            semesters = listOf(
                Semester(fullName = "2022년 1학기"),
                Semester(fullName = "2022년 2학기"),
                Semester(fullName = "2023년 1학기"),
                Semester(fullName = "2023년 2학기"),
            ),
            semesterCoursesMap = mapOf(
                "2022년 1학기" to listOf(
                    LectureInfo(tier = Tier("A+"), name = "가나다", credit = 3.toCredit(), "라마바"),
                    LectureInfo(tier = Tier("P"), name = "섬리", credit = 1.toCredit(), "라마바"),
                ),
                "2022년 2학기" to listOf(
                    LectureInfo(tier = Tier("B+"), name = "가나다", credit = 3.toCredit(), "라마바"),
                    LectureInfo(tier = Tier("F"), name = "섬리", credit = 1.toCredit(), "라마바"),
                ),
                "2023년 1학기" to listOf(
                    LectureInfo(tier = Tier("C+"), name = "가나다", credit = 3.toCredit(), "라마바"),
                    LectureInfo(tier = Tier("P"), name = "섬리", credit = 1.toCredit(), "라마바"),
                ),
                "2023년 2학기" to listOf(
                    LectureInfo(tier = Tier("B-"), name = "가나다", credit = 3.toCredit(), "라마바"),
                    LectureInfo(tier = Tier("?"), name = "섬리", credit = 1.toCredit(), "라마바"),
                ),
            ),
            captureController = rememberCaptureController(),
            captureFlag = CaptureFlag.None,
        )
    }
}
