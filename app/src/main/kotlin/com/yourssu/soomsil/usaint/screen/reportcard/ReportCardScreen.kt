package com.yourssu.soomsil.usaint.screen.reportcard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.screen.home.components.ReportOutline
import com.yourssu.soomsil.usaint.ui.component.Chart
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun ReportCardScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportCardViewModel = hiltViewModel(),
) {
    val reportCardUiState by viewModel.reportCardUiState.collectAsStateWithLifecycle()

    ReportCardScreen(
        reportCardUiState = reportCardUiState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportCardScreen(
    reportCardUiState: ReportCardUiState,
    modifier: Modifier = Modifier,
) {
    val isReportCardLoading = reportCardUiState is ReportCardUiState.Loading

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = "성적") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {

        }
    }
}

@Composable
fun ChartSummary(
    reportCardUiState: ReportCardUiState,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        when (reportCardUiState) {
            is ReportCardUiState.Loading -> Unit
            is ReportCardUiState.ReportCard -> {
                val semesters = reportCardUiState.semesterWithLectures.keys.toList()
                val summary = reportCardUiState.summary

                if (semesters.isNotEmpty()) {
                    Chart(
                        semesters = semesters,
                        modifier = Modifier.height(200.dp),
                    )
                }
                ReportOutline(
                    title = "평균학점",
                    actualValue = summary.gradePointsAverage.toString(),
                    maxValue = 4.5f.toString(),
                )
                ReportOutline(
                    title = "취득학점",
                    actualValue = summary.earnedCredits.toString(),
                    maxValue = 133.toString(), // TODO
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ReportCardScreenPreview() {
    SoomsilUSaintTheme {
        ReportCardScreen(
            reportCardUiState = ReportCardUiState.ReportCard(
                summary = ReportCardSummaryData.previewData,
                semesterWithLectures = emptyMap(),
            )
        )
    }
}
