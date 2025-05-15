package com.yourssu.soomsil.usaint.navigation

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
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

@Composable
fun USaintNavHost(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier
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
                    it.launchUrl(context, Uri.parse(url))
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
            navigateToChapel = {
                navController.navigateToTopLevelDestination(
                    TopLevelDestination.CHAPEL
                )
            }
        )
        reportCardScreen()
        chapelScreen()
    }
}
