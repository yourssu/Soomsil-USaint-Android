package com.yourssu.soomsil.usaint.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import com.yourssu.soomsil.usaint.data.analytics.MixpanelTracker
import com.yourssu.soomsil.usaint.navigation.TopLevelDestination
import com.yourssu.soomsil.usaint.navigation.USaintNavHost
import com.yourssu.soomsil.usaint.navigation.navigateToTopLevelDestination
import kotlinx.coroutines.launch
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

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            currentTopLevelDestination?.let {
                TopAppBar(title = { Text(text = it.title) })
            }
        },
        bottomBar = {
            if (currentTopLevelDestination != null) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        val selected = currentDestination.isRouteInHierarchy(destination.route)
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.label,
                                )
                            },
                            label = { Text(destination.label) },
                            selected = selected,
                            onClick = {
                                coroutineScope.launch {
                                    if (destination == TopLevelDestination.REPORT_CARD) {
                                        mixpanelTracker.trackNavigate("BOTTOM_NAV", false)
                                    } else if (destination == TopLevelDestination.CHAPEL) {
                                        mixpanelTracker.trackNavigate("BOTTOM_NAV", true)
                                    }
                                }
                                navController.navigateToTopLevelDestination(destination)
                            },
                        )
                    }
                }
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
