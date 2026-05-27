package com.yourssu.soomsil.usaint.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.data.analytics.MixpanelTracker
import com.yourssu.soomsil.usaint.navigation.TopLevelDestination
import com.yourssu.soomsil.usaint.navigation.USaintNavHost
import com.yourssu.soomsil.usaint.screen.chapel.navigation.Chapel
import com.yourssu.soomsil.usaint.screen.main.navigation.Main
import com.yourssu.soomsil.usaint.screen.mypage.navigation.MyPage
import com.yourssu.soomsil.usaint.screen.pushnotifications.navigation.PushNotifications
import com.yourssu.soomsil.usaint.ui.components.TabBar
import com.yourssu.soomsil.usaint.ui.components.navigation.TabBarDestination
import com.yourssu.soomsil.usaint.ui.components.navigation.rememberTabBarNavigationState
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun USaintApp(
    startDestination: Any,
    mixpanelTracker: MixpanelTracker,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryFlow.collectAsState(initial = null)
    val currentDestination = currentEntry?.destination

    val currentTopLevelDestination = TopLevelDestination.entries.firstOrNull { destination ->
        currentDestination?.hasRoute(route = destination.route) == true
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val tabBarDestinations = remember {
        listOf(
            TabBarDestination(
                route = requireNotNull(Main::class.qualifiedName),
                label = "홈",
                iconId = R.drawable.ic_tabbar_house,
            ),
            TabBarDestination(
                route = requireNotNull(Chapel::class.qualifiedName),
                label = "소식",
                iconId = R.drawable.ic_tabbar_megaphone,
            ),
            TabBarDestination(
                route = requireNotNull(PushNotifications::class.qualifiedName),
                label = "알림",
                iconId = R.drawable.ic_tabbar_bell,
            ),
            TabBarDestination(
                route = requireNotNull(MyPage::class.qualifiedName),
                label = "마이",
                iconId = R.drawable.ic_tabbar_user,
            ),
        )
    }
    val tabBarNavigationState = rememberTabBarNavigationState(navController, tabBarDestinations)
    val showTabBar = tabBarNavigationState.selectedRoute != null

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            currentTopLevelDestination?.let {
                TopAppBar(title = { Text(text = it.title) })
            }
        },
        bottomBar = {
            if (showTabBar) {
                TabBar(
                    items = tabBarDestinations,
                    selectedRoute = tabBarNavigationState.selectedRoute,
                    onItemSelected = tabBarNavigationState.onDestinationSelected,
                )
            }
        },
    ) { padding ->
        USaintNavHost(
            navController = navController,
            startDestination = startDestination,
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
        )
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false
