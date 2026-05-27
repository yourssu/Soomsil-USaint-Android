package com.yourssu.soomsil.usaint.screen.grade.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.screen.grade.GradeDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data object GradeDetail

fun NavHostController.navigateToGradeDetail(navOptions: NavOptions? = null) =
    navigate(GradeDetail, navOptions)

fun NavGraphBuilder.gradeDetailScreen(
    navigateToBack: () -> Unit,
) {
    composable<GradeDetail> {
        GradeDetailScreen(
            onBackClick = navigateToBack,
        )
    }
}
