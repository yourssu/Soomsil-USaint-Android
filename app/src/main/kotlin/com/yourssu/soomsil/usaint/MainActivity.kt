package com.yourssu.soomsil.usaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import androidx.room.Update
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.soomsil.usaint.screen.USaintNavHost
import com.yourssu.soomsil.usaint.screen.home.navigation.Home
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        // 네트워트 연결시에만 실행하는 제약
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 3시간에 한번씩 Worker 실행
        val periodicRequest = PeriodicWorkRequestBuilder<UpdateWorker>(3, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        // WorkManager에 등록 -> 앱이 백그라운드/포그라운드여도 시스템이 주기적으로 실행
        WorkManager.getInstance(this).enqueue(periodicRequest)

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