package com.yourssu.soomsil.usaint.screen.semesterdetail

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.domain.type.SemesterType
import com.yourssu.soomsil.usaint.screen.semesterdetail.components.SemesterDetailItem
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import com.yourssu.soomsil.usaint.ui.types.LectureInfo
import com.yourssu.soomsil.usaint.ui.types.Semester
import com.yourssu.soomsil.usaint.ui.types.Tier
import com.yourssu.soomsil.usaint.ui.types.toCredit
import com.yourssu.soomsil.usaint.util.Capturable
import com.yourssu.soomsil.usaint.util.CaptureController
import com.yourssu.soomsil.usaint.util.rememberCaptureController
import com.yourssu.soomsil.usaint.util.saveBitmapUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

//    LaunchedEffect(lifecycleOwner.lifecycle) {
//        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//            viewModel.uiEvent.collect { uiEvent ->
//                when (uiEvent) {
//                    is UiEvent.Failure -> {
//                        Toast.makeText(
//                            context,
//                            uiEvent.msg ?: context.resources.getString(R.string.error_unknown),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                    is UiEvent.SessionFailure -> {
//                        Toast.makeText(context, R.string.error_session_failure, Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
//        }
//    }

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
                    Toast.makeText(context, "캡처 도중 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
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
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            selectedTabIndex = pagerState.currentPage
    }

    LaunchedEffect(pagerState.currentPage, semesters) {
        // 현재 페이지의 강의 정보가 비어있으면 자동 refresh
        if (pagerState.currentPage in semesters.indices)
            onInitialRefresh(semesters[pagerState.currentPage].type)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "상세성적")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                                contentDescription = "back",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showBottomSheet = true }) {
                            Icon(
                                imageVector = Icons.Outlined.AddPhotoAlternate,
                                contentDescription = "Add to photo",
                            )
                        }
                        IconButton(onClick = {
                            onRefresh(semesters[pagerState.currentPage].type)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = "refresh",
                            )
                        }
                    }
                )
                if (semesters.isNotEmpty()) {
                    SecondaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
                        semesters.forEachIndexed { index, semester ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(text = semester.type.fullName.substring(2))
                                }
                            )
                        }
                    }
                }
            }
        },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                onRefresh(semesters[pagerState.currentPage].type)
            },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
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
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false },
        ) {
            Column {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCaptureFlagChanged(CaptureFlag.Original)
                            captureController.capture() // capture 이벤트 요청
                            coroutineScope
                                .launch { sheetState.hide() }
                                .invokeOnCompletion {
                                    if (!sheetState.isVisible) showBottomSheet = false
                                }
                        },
                    headlineContent = { Text(text = "원본으로 저장") },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                )
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCaptureFlagChanged(CaptureFlag.HidingInfo)
                            captureController.capture() // capture 이벤트 요청
                            coroutineScope
                                .launch { sheetState.hide() }
                                .invokeOnCompletion {
                                    if (!sheetState.isVisible) showBottomSheet = false
                                }
                        },
                    headlineContent = { Text(text = "강의정보 가리고 저장") },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
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

    SoomsilUSaintTheme {
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
