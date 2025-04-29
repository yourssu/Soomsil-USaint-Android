package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.screen.home.components.ActionTitleItem
import com.yourssu.soomsil.usaint.screen.home.components.ReportCardItem
import com.yourssu.soomsil.usaint.screen.home.components.StudentDataItem
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onReportCardClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    HomeScreen(
        homeUiState = homeUiState,
        onProfileClick = onProfileClick,
        onSettingClick = onSettingClick,
        onReportCardClick = onReportCardClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    homeUiState: HomeUiState,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onReportCardClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "유세인트",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp,
                ),
        ) {
            val studentData =
                if (homeUiState is HomeUiState.Home) homeUiState.studentData else null
            val reportCardSummaryData =
                if (homeUiState is HomeUiState.Home) homeUiState.reportCardSummaryData else null

            StudentDataItem(
                studentData = studentData,
                onProfileClick = onProfileClick,
                onSettingClick = onSettingClick,
            )

            Spacer(Modifier.height(4.dp))
            Text(
                text = "내 성적",
                modifier = Modifier.padding(vertical = 4.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (studentData?.status == "재학") {
                Spacer(Modifier.height(8.dp))
                ActionTitleItem(
                    title = "이번 학기 성적 확인",
                    onClick = { /* TODO */ },
                )
            }

            Spacer(Modifier.height(8.dp))
            ReportCardItem(
                reportCardSummary = reportCardSummaryData,
                onReportCardClick = onReportCardClick,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HomePreview_being() {
    // 재학 상태
    SoomsilUSaintTheme {
        HomeScreen(
            homeUiState = HomeUiState.Home(
                studentData = StudentData.previewData,
                reportCardSummaryData = ReportCardSummaryData.previewData,
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
            homeUiState = HomeUiState.Home(
                studentData = StudentData.previewData.copy(status = "휴학"),
                reportCardSummaryData = ReportCardSummaryData.previewData,
            ),
        )
    }
}
