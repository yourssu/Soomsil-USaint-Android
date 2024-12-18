package com.yourssu.soomsil.usaint.screen.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.screen.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
data object Login

fun NavHostController.navigateToLogin(navOptions: NavOptions?) = navigate(Login, navOptions)

fun NavGraphBuilder.loginScreen(
    navigateToHome: () -> Unit,
    navigateToBack: () -> Unit,
) {
    composable<Login> {
        // TODO route로 수정 필요
        LoginScreen(
            navigateToHome = navigateToHome,
            onBackClick = navigateToBack,
            // TODO 아래는 viewModel로 옮기기
            studentId = "",
            password = "",
            onStudentIdChange = {},
            onPasswordChange = {},
        )
    }
}

