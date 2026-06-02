package com.yourssu.soomsil.usaint.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.screen.main.components.ChapelCard
import com.yourssu.soomsil.usaint.screen.main.components.GpaChartCard
import com.yourssu.soomsil.usaint.screen.main.components.GpaHeroCard
import com.yourssu.soomsil.usaint.screen.main.components.MainHeader
import com.yourssu.soomsil.usaint.screen.main.components.ProfileCard
import com.yourssu.soomsil.usaint.screen.main.model.GpaBarData
import com.yourssu.soomsil.usaint.ui.components.GradeBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onGradeDetailClick: () -> Unit = {},
    onChartDetailClick: () -> Unit = {},
    onChapelClick: () -> Unit = {},
    tabBar: @Composable () -> Unit = {},
    viewModel: MainViewModel = hiltViewModel(),
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showGradeSheet by remember { mutableStateOf(false) }

    MainPageScreen(
        greetingName = uiState.greetingName,
        notificationCount = uiState.notificationCount,
        profileName = uiState.profileName,
        department = uiState.department,
        year = uiState.year,
        status = uiState.status,
        studentId = uiState.studentId,
        gpa = uiState.gpa,
        maxGpa = uiState.maxGpa,
        barData = uiState.barData,
        chapelAttended = uiState.chapelAttended,
        chapelTotal = uiState.chapelTotal,
        chapelProgress = uiState.chapelProgress,
        // 이번 학기 성적보기 → 이번 학기 성적 바텀시트
        onGradeDetailClick = { showGradeSheet = true },
        // 전체 학기 추이 자세히 → 성적 상세 페이지
        onChartDetailClick = onChartDetailClick,
        onChapelClick = onChapelClick,
        tabBar = tabBar,
    )

    if (showGradeSheet) {
        GradeBottomSheet(
            term = uiState.currentSemesterTerm,
            registered = uiState.currentSemesterRegistered,
            averageGrade = uiState.currentSemesterGpa,
            earnedCredits = uiState.currentSemesterCredits,
            courseCount = uiState.currentSemesterCourseCount,
            courses = uiState.currentSemesterCourses,
            onLeave = uiState.currentSemesterOnLeave,
            onDismissRequest = { showGradeSheet = false },
        )
    }
}

@Composable
@Preview
private fun MainScreenPreview() {
    MainPageScreen(tabBar = {})
}

// ─── Screen ───

@Composable
private fun MainPageScreen(
    greetingName: String = "강우현",
    notificationCount: Int = 3,
    profileName: String = "강우현",
    department: String = "컴퓨터학부",
    year: String = "2학년",
    status: String = "재학",
    studentId: String = "20231234",
    gpa: String = "3.87",
    maxGpa: String = "4.5",
    barData: List<GpaBarData> = listOf(
        GpaBarData("1-1", 40.dp),
        GpaBarData("1-2", 50.dp),
        GpaBarData("2-1", 46.dp),
        GpaBarData("2-2", 60.dp),
        GpaBarData("3-1", 80.dp, isCurrent = true, gpaText = "4.21")
    ),
    chapelAttended: Int = 5,
    chapelTotal: Int = 8,
    chapelProgress: Float = 0.625f,
    onGradeDetailClick: () -> Unit = {},
    onChartDetailClick: () -> Unit = {},
    onChapelClick: () -> Unit = {},
    onTabClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    tabBar: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        MainHeader(
            greetingName = greetingName,
            notificationCount = notificationCount
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileCard(
                name = profileName,
                department = department,
                year = year,
                status = status,
                studentId = studentId
            )
            GpaHeroCard(
                gpa = gpa,
                maxGpa = maxGpa,
                onDetailClick = onGradeDetailClick
            )
            GpaChartCard(
                bars = barData,
                onDetailClick = onChartDetailClick
            )
            ChapelCard(
                attended = chapelAttended,
                total = chapelTotal,
                progress = chapelProgress,
                onClick = onChapelClick
            )
        }

        tabBar()
    }
}