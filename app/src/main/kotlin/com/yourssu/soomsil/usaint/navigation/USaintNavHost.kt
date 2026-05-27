package com.yourssu.soomsil.usaint.navigation

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.yourssu.soomsil.usaint.screen.chapel.navigation.chapelScreen
import com.yourssu.soomsil.usaint.screen.home.navigation.homeScreen
import com.yourssu.soomsil.usaint.screen.home.navigation.navigateToHome
import com.yourssu.soomsil.usaint.screen.login.navigation.loginScreen
import com.yourssu.soomsil.usaint.screen.login.navigation.navigateToLogin
import com.yourssu.soomsil.usaint.screen.reportcard.navigation.reportCardScreen
import com.yourssu.soomsil.usaint.screen.setting.navigation.navigateToSetting
import com.yourssu.soomsil.usaint.screen.setting.navigation.settingScreen
import com.yourssu.soomsil.usaint.screen.pushnotifications.navigation.pushNotificationsScreen
import com.yourssu.soomsil.usaint.screen.main.navigation.mainScreen
import com.yourssu.soomsil.usaint.screen.main.navigation.navigateToMain
import com.yourssu.soomsil.usaint.screen.mypage.navigation.myPageScreen
import com.yourssu.soomsil.usaint.screen.grade.navigation.gradeDetailScreen
import com.yourssu.soomsil.usaint.screen.grade.navigation.navigateToGradeDetail
import androidx.core.net.toUri

@Composable
fun USaintNavHost(
    navController: NavHostController,
    startDestination: Any,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination,
    ) {
        loginScreen(
            navigateToMain = {
                navController.navigateToMain(
                    navOptions = navOptions {
                        popUpTo(navController.graph.id) {
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
        settingScreen(
            navigateToBack = {
                navController.navigateUp()
            },
            navigateToWebView = { url ->
                CustomTabsIntent.Builder().build().also {
                    it.launchUrl(context, url.toUri())
                }
            },
            navigateToLogin = {
                navController.navigateToLogin(
                    navOptions = navOptions {
                        popUpTo(navController.graph.id) {
                            saveState = false
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                )
            }
        )
        homeScreen(
            navigateToSetting = { navController.navigateToSetting() },
            navigateToSemesterList = {
                navController.navigateToTopLevelDestination(
                    TopLevelDestination.REPORT_CARD
                )
            },
            snackbarHostState = snackbarHostState,
            navigateToChapel = {
                navController.navigateToTopLevelDestination(
                    TopLevelDestination.CHAPEL
                )
            }
        )
        reportCardScreen(
            snackbarHostState = snackbarHostState
        )
        chapelScreen(
            snackbarHostState = snackbarHostState
        )
        pushNotificationsScreen()
        myPageScreen(
            navigateToBack = { navController.navigateUp() }
        )
        gradeDetailScreen(
            navigateToBack = { navController.navigateUp() }
        )
        mainScreen(
            navigateToGradeDetail = { navController.navigateToGradeDetail() }
        )
    }
}
