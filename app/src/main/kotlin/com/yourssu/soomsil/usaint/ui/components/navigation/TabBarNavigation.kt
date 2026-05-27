package com.yourssu.soomsil.usaint.ui.components.navigation

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions

data class TabBarDestination(
    val route: String,
    val label: String,
    @DrawableRes val iconId: Int,
)

data class TabBarNavigationState(
    val selectedRoute: String?,
    val onDestinationSelected: (TabBarDestination) -> Unit,
)

@Composable
fun rememberTabBarNavigationState(
    navController: NavHostController,
    destinations: List<TabBarDestination>,
    onNavigate: (String, NavOptionsBuilder.() -> Unit) -> Unit = { route, builder ->
        navController.navigate(route, builder)
    },
): TabBarNavigationState {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val selectedRoute = destinations.firstOrNull { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    }?.route

    val onSelect: (TabBarDestination) -> Unit = { destination ->
        onNavigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    return TabBarNavigationState(
        selectedRoute = selectedRoute,
        onDestinationSelected = onSelect,
    )
}
