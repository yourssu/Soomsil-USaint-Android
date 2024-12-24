package com.yourssu.soomsil.usaint.screen.semesterdetail

import android.graphics.Bitmap
import android.widget.Toast
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.ListItem
import com.yourssu.design.system.compose.atom.TopBarButton
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.design.system.compose.component.ScrollableTabBar
import com.yourssu.design.system.compose.component.Tab
import com.yourssu.design.system.compose.component.topbar.TopBar
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.domain.type.SemesterType
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.LectureInfo
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.Tier
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.util.Capturable
import com.yourssu.soomsil.usaint.util.CaptureController
import com.yourssu.soomsil.usaint.util.rememberCaptureController
import com.yourssu.soomsil.usaint.util.saveBitmapUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.yourssu.design.R as YdsR

@Composable
fun SemesterDetailScreen(
    initialTabIndex: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SemesterDetailViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var captureFlag: CaptureFlag by remember { mutableStateOf(CaptureFlag.None) }
    val captureController = rememberCaptureController()

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { uiEvent ->
                when (uiEvent) {
                    is UiEvent.Failure -> {
                        Toast.makeText(
                            context,
                            uiEvent.msg ?: context.resources.getString(R.string.error_unknown),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is UiEvent.SessionFailure -> {
                        Toast.makeText(context, R.string.error_session_failure, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    SemesterDetailScreen(
        isRefreshing = viewModel.isRefreshing,
        onRefresh = viewModel::refresh,
        onInitialRefresh = viewModel::initialRefresh,
        initialTabIndex = initialTabIndex,
        semesters = viewModel.semesters,
        semesterLecturesMap = viewModel.semesterLecturesMap,
        captureController = captureController,
        captureFlag = captureFlag,
        onBackClick = onBackClick,
        onCaptureFlagChanged = { flag -> captureFlag = flag },
        onCaptured = { semesterName, bitmap ->
            saveBitmapUtil(
                bitmap = bitmap,
                context = context,
                filename = "soomsil_report_${System.currentTimeMillis()}.png",
                onSuccess = {
                    Toast.makeText(context, "$semesterName 이미지를 저장했습니다.", Toast.LENGTH_SHORT).show()
                    captureFlag = CaptureFlag.None
                },
                onError = {
                    Toast.makeText(context, R.string.error_capture_fail, Toast.LENGTH_SHORT).show()
                    captureFlag = CaptureFlag.None
                },
            )
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterDetailScreen(
    isRefreshing: Boolean,
    onRefresh: (SemesterType) -> Unit,
    onInitialRefresh: (SemesterType) -> Unit,
    initialTabIndex: Int,
    semesters: List<Semester>,
    semesterLecturesMap: Map<SemesterType, List<LectureInfo>>,
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

    LaunchedEffect(initialTabIndex, semesters) {
        pagerState.scrollToPage(initialTabIndex)
    }

    LaunchedEffect(pagerState.currentPage, semesters) {
        // 현재 페이지의 강의 정보가 비어있으면 자동 refresh
        if (pagerState.currentPage in semesters.indices)
            onInitialRefresh(semesters[pagerState.currentPage].type)
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
                            onRefresh(semesters[pagerState.currentPage].type)
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
                                text = semester.type.fullName.substring(2),
                            )
                        }
                    }
                }
            }
        },
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                onRefresh(semesters[pagerState.currentPage].type)
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            HorizontalPager(state = pagerState) { pagerIdx ->
                val semester = semesters[pagerIdx]
                semesterLecturesMap[semester.type]?.let { courses ->
                    Capturable(
                        controller = captureController,
                        predicate = { pagerState.currentPage == pagerIdx },
                        onCaptured = { bitmap -> onCaptured(semester.type.fullName, bitmap) },
                        modifier = Modifier
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
            onInitialRefresh = {},
            initialTabIndex = 0,
            semesters = listOf(
                Semester(type = SemesterType.One(2022)),
                Semester(type = SemesterType.Two(2022)),
                Semester(type = SemesterType.One(2023)),
                Semester(type = SemesterType.Two(2023)),
//                Semester(fullName = "2022년 1학기"),
//                Semester(fullName = "2022년 2학기"),
//                Semester(fullName = "2023년 1학기"),
//                Semester(fullName = "2023년 2학기"),
            ),
            semesterLecturesMap = mapOf(
                SemesterType.One(2022) to listOf(
                    LectureInfo(tier = Tier("A+"), name = "가나다", credit = 3.toCredit(), "라마바"),
                    LectureInfo(tier = Tier("P"), name = "섬리", credit = 1.toCredit(), "라마바"),
                ),
                SemesterType.Two(2022) to listOf(
                    LectureInfo(tier = Tier("B+"), name = "가나다", credit = 3.toCredit(), "라마바"),
                    LectureInfo(tier = Tier("F"), name = "섬리", credit = 1.toCredit(), "라마바"),
                ),
                SemesterType.Two(2023) to listOf(
                    LectureInfo(tier = Tier("C+"), name = "가나다", credit = 3.toCredit(), "라마바"),
                    LectureInfo(tier = Tier("P"), name = "섬리", credit = 1.toCredit(), "라마바"),
                ),
                SemesterType.Two(2023) to listOf(
                    LectureInfo(tier = Tier("B-"), name = "가나다", credit = 3.toCredit(), "라마바"),
                    LectureInfo(tier = Tier("?"), name = "섬리", credit = 1.toCredit(), "라마바"),
                ),
            ),
            captureController = rememberCaptureController(),
            captureFlag = CaptureFlag.None,
        )
    }
}
