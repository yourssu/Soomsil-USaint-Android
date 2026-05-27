package com.yourssu.soomsil.usaint.screen.mypage.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yourssu.soomsil.usaint.screen.mypage.MyPageScreen
import kotlinx.serialization.Serializable

@Serializable
data object MyPage

fun NavHostController.navigateToMyPage(navOptions: NavOptions? = null) = navigate(MyPage, navOptions)

fun NavGraphBuilder.myPageScreen(
    navigateToBack: () -> Unit,
) {
    composable<MyPage> {
        MyPageScreen(
            onBackClick = navigateToBack,
        )
    }
}
