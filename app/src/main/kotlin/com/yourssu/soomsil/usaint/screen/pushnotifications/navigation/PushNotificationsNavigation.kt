package com.yourssu.soomsil.usaint.screen.pushnotifications.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.screen.pushnotifications.PushNotificationsScreen
import kotlinx.serialization.Serializable

@Serializable
data object PushNotifications

fun NavHostController.navigateToPushNotifications(navOptions: NavOptions? = null) =
    navigate(PushNotifications, navOptions)

fun NavGraphBuilder.pushNotificationsScreen() {
    composable<PushNotifications> {
        PushNotificationsScreen()
    }
}
