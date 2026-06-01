package com.yourssu.soomsil.usaint.screen.grade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.screen.grade.components.CourseDetailCard
import com.yourssu.soomsil.usaint.screen.grade.components.GpaDetailCard
import com.yourssu.soomsil.usaint.screen.grade.components.GpaTrendChart
import com.yourssu.soomsil.usaint.screen.grade.components.GradeDetailHeader
import com.yourssu.soomsil.usaint.screen.grade.components.SemesterTabs
import com.yourssu.soomsil.usaint.screen.grade.model.CourseItem
import com.yourssu.soomsil.usaint.screen.grade.model.GpaPoint
import com.yourssu.soomsil.usaint.screen.grade.model.SemesterTab

@Composable
@Preview
private fun GradeDetailScreenPreview() {
    GradeDetailScreen(
        onBackClick = {},
        semesters = listOf(
            SemesterTab("2025년 1학기", isActive = true),
            SemesterTab("2024년 2학기"),
            SemesterTab("2024년 1학기"),
            SemesterTab("2023년 2학기")
        ),
        gpaPoints = listOf(
            GpaPoint("24-1", 3.2f),
            GpaPoint("24-2", 3.5f),
            GpaPoint("25-1", 3.7f),
            GpaPoint("25-2", 3.87f, isCurrent = true)
        ),
        courses = listOf(
            CourseItem("비전채플", "박영수", "0.5학점", "P", Color(0xFF0062FF), Color(0xFF0062FF), Color(0xFFE6F0FF)),
            CourseItem("CTE for IT, Engineering", "최민지", "3.0학점", "A+", Color(0xFF059669), Color(0xFF059669), Color(0xFFECFDF5)),
            CourseItem("인간관계론", "이준호", "2.0학점", "A-", Color(0xFF0062FF), Color(0xFF0062FF), Color(0xFFE6F0FF)),
        ),
        gpa = "3.87",
        maxGpa = "4.5",
        credits = "11.5",
        courseCount = "5",
        rank = "12위",
        onTabClick = {},
    )
}

// ─── Screen (ViewModel 연결) ───

@Composable
fun GradeDetailScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GradeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GradeDetailScreen(
        onBackClick = onBackClick,
        semesters = uiState.semesters,
        gpaPoints = uiState.gpaPoints,
        courses = uiState.courses,
        gpa = uiState.gpa,
        maxGpa = uiState.maxGpa,
        credits = uiState.credits,
        courseCount = uiState.courseCount,
        rank = uiState.rank,
        onTabClick = viewModel::onTabClick,
        modifier = modifier,
    )
}

// ─── Screen (stateless) ───

@Composable
fun GradeDetailScreen(
    onBackClick: () -> Unit,
    semesters: List<SemesterTab>,
    gpaPoints: List<GpaPoint>,
    courses: List<CourseItem>,
    gpa: String,
    maxGpa: String,
    credits: String,
    courseCount: String,
    rank: String,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        GradeDetailHeader(onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SemesterTabs(
                tabs = semesters,
                onTabClick = onTabClick
            )

            GpaDetailCard(
                gpa = gpa,
                maxGpa = maxGpa,
                credits = credits,
                courseCount = courseCount,
                rank = rank
            )

            GpaTrendChart(points = gpaPoints)

            courses.forEach { course ->
                CourseDetailCard(course = course)
            }
        }
    }
}