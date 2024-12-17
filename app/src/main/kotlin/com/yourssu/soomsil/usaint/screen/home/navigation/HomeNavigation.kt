package com.yourssu.soomsil.usaint.screen.home.navigation

import androidx.navigation.NavController
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
){
    composable<Home> {
        // TODO route로 수정 필요
        HomeScreen(
            onProfileClick = navigateToSetting,
            onSettingClick = navigateToSetting,
            onGradeCardClick = navigateToSemesterList,
            // TODO 아래는 viewModel로 옮기기
            userName = "",
            userInfo = "",
        )
    }
}