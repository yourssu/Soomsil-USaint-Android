package com.yourssu.soomsil.usaint.screen.semesterdetail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.yourssu.soomsil.usaint.screen.semesterdetail.SemesterDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class SemesterDetail(val initialTabIndex: Int)

fun NavHostController.navigateToSemesterDetail(
    initialTabIndex: Int,
    navOptions: NavOptions? = null
) = navigate(SemesterDetail(initialTabIndex), navOptions)


fun NavGraphBuilder.semesterDetailScreen(
    navigateToBack: () -> Unit,
) {
    composable<SemesterDetail> {
        val args = it.toRoute<SemesterDetail>()
        SemesterDetailScreen(
            onBackClick = navigateToBack,
            initialTabIndex = args.initialTabIndex,
        )
    }
}