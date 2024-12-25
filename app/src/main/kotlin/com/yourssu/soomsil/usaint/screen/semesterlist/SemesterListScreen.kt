package com.yourssu.soomsil.usaint.screen.semesterlist

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.CheckBox
import com.yourssu.design.system.compose.atom.Divider
import com.yourssu.design.system.compose.atom.Thickness
import com.yourssu.design.system.compose.atom.TopBarButton
import com.yourssu.design.system.compose.base.Icon
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.design.system.compose.base.ydsClickable
import com.yourssu.design.system.compose.component.topbar.TopBar
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.domain.type.makeSemesterType
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.component.chart.Chart
import com.yourssu.soomsil.usaint.ui.component.chart.ChartData
import com.yourssu.soomsil.usaint.ui.entities.Grade
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import com.yourssu.design.R as YdsR

@Composable
fun SemesterListScreen(
    onBackClick: () -> Unit,
    onGradeListClick: (initialTabIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SemesterListViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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
                        Toast.makeText(
                            context,
                            R.string.error_session_failure,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is UiEvent.RefreshFailure -> {
                        Toast.makeText(
                            context,
                            R.string.error_refresh_failure,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Timber.d("SemesterListScreen DisposableEffect ::: cancelJob")
            viewModel.cancelJob()
        }
    }

    SemesterListScreen(
        isRefreshing = viewModel.isRefreshing,
        onRefresh = viewModel::refresh,
        reportCardSummary = viewModel.reportCardSummary,
        semesters = viewModel.semesters,
        includeSeasonalSemester = viewModel.includeSeasonalSemester,
        onSeasonalFlagChange = { viewModel.includeSeasonalSemester = it },
        onBackClick = onBackClick,
        onGradeListClick = onGradeListClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterListScreen(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    semesters: List<Semester>,
    includeSeasonalSemester: Boolean,
    onSeasonalFlagChange: (Boolean) -> Unit,
    reportCardSummary: ReportCardSummary,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onGradeListClick: (initialTabIndex: Int) -> Unit = {},
) {
    val appliedSemesters = if (includeSeasonalSemester) {
        semesters
    } else {
        semesters.filter { !it.type.isSeasonal }
    }

    YdsScaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                navigationIcon = {
                    TopBarButton(
                        icon = YdsR.drawable.ic_arrow_left_line,
                        onClick = onBackClick,
                    )
                },
                title = stringResource(id = R.string.reportcard_title),
            )
        },
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 20.dp,
                        ),
                ) {
                    ScoreDetail(
                        title = stringResource(id = R.string.reportcard_average_grade),
                        actualValue = reportCardSummary.gpa.formatToString(),
                        maxValue = Grade.Max.formatToString(),
                        modifier = Modifier.weight(1f),
                    )
                    ScoreDetail(
                        title = stringResource(id = R.string.reportcard_credit),
                        actualValue = reportCardSummary.earnedCredit.formatToString(),
                        maxValue = reportCardSummary.graduateCredit.formatToString(),
                        modifier = Modifier.weight(1f),
                    )
                }
                if (appliedSemesters.isNotEmpty()) {
                    Chart(
                        chartData = ChartData(
                            semesters = appliedSemesters,
                        ),
                        modifier = Modifier
                            .height(170.dp)
                            .padding(horizontal = 28.dp),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 18.dp,
                            end = 34.dp,
                            bottom = 15.dp,
                        ),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CheckBox(
                        text = stringResource(id = R.string.reportcard_include_seasonal_semester),
                        checked = includeSeasonalSemester,
                        onCheckedChange = onSeasonalFlagChange,
                    )
                }
                Divider(thickness = Thickness.Thick)
                if (semesters.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(20.dp),
                            color = YdsTheme.colors.buttonPoint
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                    ) {
                        appliedSemesters.forEachIndexed { index, semester ->
                            SemesterReport(
                                semester = semester,
                                onClick = { onGradeListClick(index) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreDetail(
    title: String,
    actualValue: String,
    maxValue: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        YdsText(
            text = title,
            style = YdsTheme.typography.subTitle3,
            modifier = Modifier.padding(bottom = 2.dp),
        )
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            YdsText(
                text = actualValue,
                style = YdsTheme.typography.display2,
                color = YdsTheme.colors.textPointed,
            )
            YdsText(
                text = stringResource(id = R.string.grade_delimiter),
                style = YdsTheme.typography.body1,
                modifier = Modifier.padding(
                    start = 4.dp,
                    end = 4.dp,
                ),
                color = YdsTheme.colors.textTertiary,
            )
            YdsText(
                text = maxValue,
                style = YdsTheme.typography.body1,
                color = YdsTheme.colors.textTertiary,
            )
        }
    }
}

@Composable
private fun SemesterReport(
    semester: Semester,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .ydsClickable(onClick = onClick)
            .padding(
                top = 16.dp,
                bottom = 16.dp,
                start = 24.dp,
                end = 20.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp),
        ) {
            YdsText(
                text = semester.type.fullName,
                style = YdsTheme.typography.subTitle2,
            )
            YdsText(
                text = "${semester.earnedCredit.formatToString()}학점",
                style = YdsTheme.typography.body2,
                color = YdsTheme.colors.textTertiary,
            )
        }
        YdsText(
            text = semester.gpa.formatToString(),
            style = YdsTheme.typography.subTitle2,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 12.dp,
            ),
        )
        Icon(id = YdsR.drawable.ic_arrow_right_line)
    }
}

@PreviewLightDark
@Composable
private fun SemesterListScreenPreview() {
    var include by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    YdsTheme {
        SemesterListScreen(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    delay(1000)
                    isRefreshing = false
                }
            },
            semesters = listOf(
                Semester(
                    type = makeSemesterType(2022, "1"),
                    gpa = 2.9.toGrade(),
                    earnedCredit = 19.5.toCredit(),
                ),
                Semester(
                    type = makeSemesterType(2022, "2"),
                    gpa = 4.2.toGrade(),
                    earnedCredit = 19.5.toCredit(),
                ),
                Semester(
                    type = makeSemesterType(2023, "1"),
                    gpa = 3.5.toGrade(),
                    earnedCredit = 19.5.toCredit(),
                ),
                Semester(
                    type = makeSemesterType(2023, "여름"),
                    gpa = 4.5.toGrade(),
                    earnedCredit = 19.5.toCredit(),
                ),
            ),
            includeSeasonalSemester = include,
            onSeasonalFlagChange = { include = it },
            reportCardSummary = ReportCardSummary(
                gpa = 3.9.toGrade(),
                earnedCredit = 52.5.toCredit(),
                graduateCredit = 133.toCredit(),
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun SemesterListScreenPreview_empty() {
    YdsTheme {
        SemesterListScreen(
            isRefreshing = false,
            onRefresh = {},
            semesters = emptyList(),
            includeSeasonalSemester = false,
            onSeasonalFlagChange = {},
            reportCardSummary = ReportCardSummary(
                gpa = 3.9.toGrade(),
                earnedCredit = 52.5.toCredit(),
                graduateCredit = 133.toCredit(),
            ),
        )
    }
}
