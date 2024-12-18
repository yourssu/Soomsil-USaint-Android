package com.yourssu.soomsil.usaint.screen.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.screen.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavHostController.navigateToHome(navOptions: NavOptions? = null) = navigate(Home, navOptions)

fun NavGraphBuilder.homeScreen(
    navigateToSetting: () -> Unit,
    navigateToSemesterList: () -> Unit,
) {
    composable<Home> {
        HomeScreen(
            onProfileClick = navigateToSetting,
            onSettingClick = navigateToSetting,
            onReportCardClick = navigateToSemesterList,
        )
    }
}