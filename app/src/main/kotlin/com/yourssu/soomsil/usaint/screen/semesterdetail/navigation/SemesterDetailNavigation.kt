package com.yourssu.soomsil.usaint.screen.semesterdetail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.yourssu.soomsil.usaint.screen.semesterdetail.CaptureFlag
import com.yourssu.soomsil.usaint.screen.semesterdetail.SemesterDetailScreen
import com.yourssu.soomsil.usaint.util.CaptureController
import kotlinx.serialization.Serializable

@Serializable
data class SemesterDetail(val initialTabIndex: Int)

fun NavHostController.navigateToSemesterDetail(navOptions: NavOptions? = null) =
    navigate(SemesterDetail(initialTabIndex = 0), navOptions)


fun NavGraphBuilder.semesterDetailScreen(
    navigateToBack: () -> Unit,
){
    composable<SemesterDetail> {
        val args = it.toRoute<SemesterDetail>()
        SemesterDetailScreen(
            onBackClick = navigateToBack,
            initialPage = args.initialTabIndex,
            // TODO 아래는 삭제 or viewModel로 옮기기
            semesters = listOf(),
            semesterCoursesMap = mapOf(),
            captureController = CaptureController(),
            captureFlag = CaptureFlag.None,
        )
    }
}