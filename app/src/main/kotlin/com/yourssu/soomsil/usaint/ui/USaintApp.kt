package com.yourssu.soomsil.usaint.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.rememberNavController
import com.yourssu.soomsil.usaint.screen.USaintNavHost
import com.yourssu.soomsil.usaint.screen.chapel.navigation.Chapel
import com.yourssu.soomsil.usaint.screen.home.navigation.Home
import com.yourssu.soomsil.usaint.screen.reportcard.navigation.ReportCard

@Composable
fun USaintApp(
    startDestination: Any,
    modifier: Modifier = Modifier,
) {
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.School, Icons.Filled.Church)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.School, Icons.Outlined.Church)
    var selectedItem by remember { mutableIntStateOf(0) }

    val navController = rememberNavController()
    val currentDestination by navController.currentBackStackEntryFlow.collectAsState(initial = null)

    val topLevelDestination = listOf("홈" to Home, "성적" to ReportCard, "채플" to Chapel)
    val currentTopLevelDestination = topLevelDestination.firstOrNull { (_, dest) ->
        currentDestination?.destination?.hasRoute(route = dest::class) == true
    }

    // TODO currentTopLevelDestination에 따라 selectedItem 바꾸기

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (currentTopLevelDestination != null) {
                NavigationBar {
                    topLevelDestination.forEachIndexed { index, (label, dest) ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                    contentDescription = label,
                                )
                            },
                            label = { Text(label) },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                navController.navigate(dest)
                            }
                        )
                    }
                }
            }
        },
    ) { padding ->
        USaintNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
        )
    }
}