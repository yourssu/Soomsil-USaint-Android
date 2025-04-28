package com.yourssu.soomsil.usaint.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.yourssu.soomsil.usaint.screen.home.navigation.Home
import com.yourssu.soomsil.usaint.screen.home.navigation.navigateToHome
import com.yourssu.soomsil.usaint.screen.reportcard.navigation.ReportCard
import com.yourssu.soomsil.usaint.screen.reportcard.navigation.navigateToReportCard
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val route: KClass<*>,
) {
    HOME(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        label = "홈",
        route = Home::class,
    ),
    REPORT_CARD(
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School,
        label = "성적",
        route = ReportCard::class,
    ),
//    CHAPEL(
//        selectedIcon = Icons.Filled.Church,
//        unselectedIcon = Icons.Outlined.Church,
//        label = "채플",
//        route = Chapel::class,
//    )
}

fun NavHostController.navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
    val topLevelNavOptions = navOptions {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
    when (topLevelDestination) {
        TopLevelDestination.HOME -> this.navigateToHome(topLevelNavOptions)
        TopLevelDestination.REPORT_CARD -> this.navigateToReportCard(topLevelNavOptions)
//        TopLevelDestination.CHAPEL -> this.navigateToChapel(topLevelNavOptions)
    }
}
