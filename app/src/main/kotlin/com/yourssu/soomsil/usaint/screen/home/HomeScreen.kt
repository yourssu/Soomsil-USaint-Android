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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.screen.home.components.ReportCardSummaryItem
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
fun HomeScreen(
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
                        text = stringResource(R.string.saint_title),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
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
            Spacer(Modifier.height(12.dp))
            ReportCardSummaryItem(
                studentData = studentData,
                reportCardSummary = reportCardSummaryData,
                onReportCardClick = onReportCardClick,
            )
//            Spacer(Modifier.height(12.dp))
//            ChapelCardItem(
//                onChapelCardClick = {},
//                chapelInfo = ChapelInfo(
//                    "D-11-4",
//                    "(월) 13:30~14:20",
//                    12,
//                    6
//                )
//            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HomePreview() {
    SoomsilUSaintTheme {
        HomeScreen(
            homeUiState = HomeUiState.Home(
                studentData = StudentData.previewData,
                reportCardSummaryData = ReportCardSummaryData.previewData,
            ),
        )
    }
}
