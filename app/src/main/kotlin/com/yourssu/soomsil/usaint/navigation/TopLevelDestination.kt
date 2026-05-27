package com.yourssu.soomsil.usaint.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.yourssu.soomsil.usaint.screen.chapel.navigation.Chapel
import com.yourssu.soomsil.usaint.screen.chapel.navigation.navigateToChapel
import com.yourssu.soomsil.usaint.screen.home.navigation.Home
import com.yourssu.soomsil.usaint.screen.home.navigation.navigateToHome
import com.yourssu.soomsil.usaint.screen.reportcard.navigation.ReportCard
import com.yourssu.soomsil.usaint.screen.reportcard.navigation.navigateToReportCard
import com.yourssu.soomsil.usaint.screen.pushnotifications.navigation.PushNotifications
import com.yourssu.soomsil.usaint.screen.pushnotifications.navigation.navigateToPushNotifications
import com.yourssu.soomsil.usaint.screen.main.navigation.Main
import com.yourssu.soomsil.usaint.screen.main.navigation.navigateToMain
import com.yourssu.soomsil.usaint.screen.mypage.navigation.MyPage
import com.yourssu.soomsil.usaint.screen.mypage.navigation.navigateToMyPage
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val route: KClass<*>,
    val title: String,
) {
    HOME(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        label = "홈",
        route = Main::class,
        title = "유세인트",
    ),
    REPORT_CARD(
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School,
        label = "성적",
        route = ReportCard::class,
        title = "성적",
    ),
    CHAPEL(
        selectedIcon = Icons.Filled.Church,
        unselectedIcon = Icons.Outlined.Church,
        label = "채플",
        route = Chapel::class,
        title = "채플",
    ),
    PUSH_NOTIFICATION(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        label = "푸시알림",
        route = PushNotifications::class,
        title = "푸시알림",
    ),
    MYPAGE(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        label = "마이",
        route = MyPage::class,
        title = "마이",
    ),
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
        TopLevelDestination.HOME -> this.navigateToMain(topLevelNavOptions)
        TopLevelDestination.REPORT_CARD -> this.navigateToReportCard(topLevelNavOptions)
        TopLevelDestination.CHAPEL -> this.navigateToChapel(topLevelNavOptions)
        TopLevelDestination.PUSH_NOTIFICATION -> this.navigateToPushNotifications(topLevelNavOptions)
        TopLevelDestination.MYPAGE -> this.navigateToMyPage(topLevelNavOptions)
    }
}
