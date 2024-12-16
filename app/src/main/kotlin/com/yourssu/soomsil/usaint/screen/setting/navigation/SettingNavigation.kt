package com.yourssu.soomsil.usaint.screen.setting.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.screen.setting.SettingScreen
import kotlinx.serialization.Serializable

@Serializable
data object Setting

fun NavHostController.navigateToSetting(navOptions: NavOptions? = null) = navigate(Setting, navOptions)

fun NavGraphBuilder.settingScreen(
    navigateToBack: () -> Unit,
){
    composable<Setting>{
        SettingScreen(
            onBackClick = navigateToBack,
        )
    }
}