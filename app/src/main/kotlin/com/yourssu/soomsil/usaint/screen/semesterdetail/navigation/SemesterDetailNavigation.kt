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
data class SemesterDetail(val semesterName: String)

fun NavHostController.navigateToSemesterDetail(navOptions: NavOptions? = null) =
    navigate(SemesterDetail(semesterName = "123123"), navOptions) // TODO args default 값 수정


fun NavGraphBuilder.semesterDetailScreen(
    navigateToBack: () -> Unit,
){
    composable<SemesterDetail> {
        val args = it.toRoute<SemesterDetail>()
        SemesterDetailScreen(
            onBackClick = navigateToBack,
            initialPageName = args.semesterName,
            // TODO 아래는 삭제 or viewModel로 옮기기
            initialPage = -1,
            semesters = listOf(),
            semesterCoursesMap = mapOf(),
            captureController = CaptureController(),
            captureFlag = CaptureFlag.None,
        )
    }
}