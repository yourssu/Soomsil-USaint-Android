package com.yourssu.soomsil.usaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import com.yourssu.soomsil.usaint.screen.navigation.USaintNavHost
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import dev.eatsteak.rusaint.ffi.USaintSessionBuilder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController: NavHostController = rememberNavController()

            LaunchedEffect(Unit) {
                USaintSessionBuilder().withPassword("20211722", "kwakkun1208!")
            }
            SoomsilUSaintTheme {
                YdsScaffold() {
                    USaintNavHost(
                        navController = navController,
                        startDestination = Login
                    )
                }
            }
        }
    }
}