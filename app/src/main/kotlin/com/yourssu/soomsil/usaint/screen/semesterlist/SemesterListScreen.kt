package com.yourssu.soomsil.usaint.screen.semesterlist

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.domain.type.SemesterType
import com.yourssu.soomsil.usaint.domain.type.makeSemesterType
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.component.chart.Chart
import com.yourssu.soomsil.usaint.ui.component.chart.ChartData
import com.yourssu.soomsil.usaint.ui.entities.Grade
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

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
        onSeasonalFlagChange = viewModel::setChartFlag,
        onBackClick = onBackClick,
        onGradeListClick = onGradeListClick,
        currentSemester = viewModel.currentSemester,
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
    currentSemester: SemesterType? = null,
    onBackClick: () -> Unit = {},
    onGradeListClick: (initialTabIndex: Int) -> Unit = {},
) {
    val chartSemesters = if (includeSeasonalSemester) {
        semesters
    } else {
        semesters.filter { !it.type.isSeasonal }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.reportcard_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                            contentDescription = "back",
                        )
                    }
                }
            )
        },
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = 24.dp,
                        vertical = 20.dp,
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                if (chartSemesters.isNotEmpty()) {
                    Chart(
                        chartData = ChartData(semesters = chartSemesters),
                        modifier = Modifier.height(170.dp),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = stringResource(R.string.reportcard_include_seasonal_semester))
                    Checkbox(
                        checked = includeSeasonalSemester,
                        onCheckedChange = onSeasonalFlagChange,
                    )
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
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(2.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = actualValue,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.grade_delimiter),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = maxValue,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SemesterListScreenPreview() {
    var include by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    SoomsilUSaintTheme {
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
    SoomsilUSaintTheme {
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
