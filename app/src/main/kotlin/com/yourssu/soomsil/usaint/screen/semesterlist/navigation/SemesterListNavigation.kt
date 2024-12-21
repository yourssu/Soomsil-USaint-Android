package com.yourssu.soomsil.usaint.screen.semesterlist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.data.type.SemesterType
import com.yourssu.soomsil.usaint.screen.semesterlist.SemesterListScreen
import kotlinx.serialization.Serializable

@Serializable
data object SemesterList

fun NavHostController.navigateToSemesterList(navOptions: NavOptions? = null) =
    navigate(SemesterList, navOptions)

fun NavGraphBuilder.semesterListScreen(
    navigateToSemesterListDetail: (initialTabIndex: Int) -> Unit,
    navigateToBack: () -> Unit,
) {
    composable<SemesterList> {
        SemesterListScreen(
            onGradeListClick = navigateToSemesterListDetail, // TODO args 넘기기
            onBackClick = navigateToBack,
        )
    }
}