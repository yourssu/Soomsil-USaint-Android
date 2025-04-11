package com.yourssu.soomsil.usaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.yourssu.soomsil.usaint.screen.USaintNavHost
import com.yourssu.soomsil.usaint.screen.home.navigation.Home
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SoomsilUSaintTheme {
                viewModel.isLoggedIn?.let { isLoggedIn ->
                    USaintNavHost(
                        navController = rememberNavController(),
                        startDestination = if (isLoggedIn) Home else Login,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .statusBarsPadding()
                    )
                }
            }
        }
    }
}