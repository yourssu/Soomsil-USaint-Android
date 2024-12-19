package com.yourssu.soomsil.usaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.soomsil.usaint.screen.home.navigation.Home
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import com.yourssu.soomsil.usaint.screen.navigation.USaintNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        setContent {
            YdsTheme(
                isDarkMode = false
            ) {
                viewModel.isLoggedIn?.let { isLoggedIn ->
                    USaintNavHost(
                        navController = rememberNavController(),
                        startDestination = if (isLoggedIn) Home else Login
                    )
                }
            }
        }
    }
}