package com.yourssu.soomsil.usaint.screen.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.screen.main.MainScreen
import kotlinx.serialization.Serializable

@Serializable
data object Main

fun NavHostController.navigateToMain(navOptions: NavOptions? = null) = navigate(Main, navOptions)

fun NavGraphBuilder.mainScreen(
    navigateToGradeDetail: () -> Unit,
) {
    composable<Main> {
        MainScreen(
            onGradeDetailClick = navigateToGradeDetail,
        )
    }
}
