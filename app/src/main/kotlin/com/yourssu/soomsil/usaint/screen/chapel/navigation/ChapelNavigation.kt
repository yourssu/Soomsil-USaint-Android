package com.yourssu.soomsil.usaint.screen.chapel.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.screen.chapel.ChapelScreen
import kotlinx.serialization.Serializable

@Serializable
data object Chapel

fun NavHostController.navigateToChapel(navOptions: NavOptions? = null) =
    navigate(Chapel, navOptions)

fun NavGraphBuilder.chapelScreen() {
    composable<Chapel> {
        ChapelScreen()
    }
}
