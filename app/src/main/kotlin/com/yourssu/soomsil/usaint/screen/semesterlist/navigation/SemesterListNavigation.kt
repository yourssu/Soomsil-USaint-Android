package com.yourssu.soomsil.usaint.screen.semesterlist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.screen.semesterlist.SemesterListScreen
import com.yourssu.soomsil.usaint.ui.component.entities.Grade
import com.yourssu.soomsil.usaint.ui.component.entities.toCredit
import kotlinx.serialization.Serializable

@Serializable
data object SemesterList

fun NavHostController.navigateToSemesterList(navOptions: NavOptions? = null) = navigate(SemesterList, navOptions)

fun NavGraphBuilder.semesterListScreen(
    navigateToSemesterListDetail: (String) -> Unit,
    navigateToBack: () -> Unit,
){
    composable<SemesterList> {
        // TODO route로 수정 필요
        SemesterListScreen(
            onGradeListClick = navigateToSemesterListDetail, // TODO args 넘기기
            onBackClick = navigateToBack,

            // TODO 아래는 viewModel로 옮기기
            onRefresh = {},
            semesters = listOf(),
            includeSeasonalSemester = true,
            onSeasonalFlagChange = {},
            overallGpa = Grade.ZERO,
            earnedCredit = 0.toCredit(),
        )
    }
}