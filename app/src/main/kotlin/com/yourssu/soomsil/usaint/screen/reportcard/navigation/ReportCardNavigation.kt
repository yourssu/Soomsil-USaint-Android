package com.yourssu.soomsil.usaint.screen.reportcard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.screen.reportcard.ReportCardScreen
import kotlinx.serialization.Serializable

@Serializable
data object ReportCard

fun NavHostController.navigateToReportCard(navOptions: NavOptions? = null) =
    navigate(ReportCard, navOptions)

fun NavGraphBuilder.reportCardScreen() {
    composable<ReportCard> {
        ReportCardScreen()
    }
}
