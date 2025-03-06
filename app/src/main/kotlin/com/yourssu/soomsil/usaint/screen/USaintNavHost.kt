package com.yourssu.soomsil.usaint.screen

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.yourssu.soomsil.usaint.screen.home.navigation.homeScreen
import com.yourssu.soomsil.usaint.screen.home.navigation.navigateToHome
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import com.yourssu.soomsil.usaint.screen.login.navigation.loginScreen
import com.yourssu.soomsil.usaint.screen.login.navigation.navigateToLogin
import com.yourssu.soomsil.usaint.screen.semesterdetail.navigation.navigateToSemesterDetail
import com.yourssu.soomsil.usaint.screen.semesterdetail.navigation.semesterDetailScreen
import com.yourssu.soomsil.usaint.screen.semesterlist.navigation.navigateToSemesterList
import com.yourssu.soomsil.usaint.screen.semesterlist.navigation.semesterListScreen
import com.yourssu.soomsil.usaint.screen.setting.navigation.navigateToSetting
import com.yourssu.soomsil.usaint.screen.setting.navigation.settingScreen

@Composable
fun USaintNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Any = Login,
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination,
    ) {
        loginScreen(
            navigateToHome = {
                navController.navigateToHome(
                    navOptions = navOptions {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                )
            },
            navigateToBack = { navController.navigateUp() },
        )

        homeScreen(
            navigateToSetting = { navController.navigateToSetting() },
            navigateToSemesterList = { navController.navigateToSemesterList() },
        )

        settingScreen(
            navigateToBack = {
                navController.navigateUp()
            },
            navigateToWebView = { url ->
                CustomTabsIntent.Builder().build().also {
                    it.launchUrl(context, Uri.parse(url))
                }
            },
            navigateToLogin = {
                navController.navigateToLogin(
                    navOptions = navOptions {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                )
            }
        )

        semesterListScreen(
            navigateToSemesterListDetail = { idx -> navController.navigateToSemesterDetail(idx) },
            navigateToBack = { navController.navigateUp() },
        )

        semesterDetailScreen(
            navigateToBack = { navController.navigateUp() },
        )
    }
}