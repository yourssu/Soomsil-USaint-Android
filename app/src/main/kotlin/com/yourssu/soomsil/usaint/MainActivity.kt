package com.yourssu.soomsil.usaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import androidx.room.Update
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.soomsil.usaint.screen.USaintNavHost
import com.yourssu.soomsil.usaint.screen.home.navigation.Home
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        // 테스트 목적으로 모든 워커를 취소
        WorkManager.getInstance(this).cancelAllWork()

        // 네트워트 연결시에만 실행하는 제약
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 3시간에 한번씩 Worker 실행
        val periodicRequest = PeriodicWorkRequestBuilder<UpdateWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        // 1734944395256
        //

        // WorkManager에 등록 -> 앱이 백그라운드/포그라운드여도 시스템이 주기적으로 실행
        // 유니크한 이름으로 등록 -> 이미 등록된 이름이 있으면 KEEP으로 유지(동일한 여러개 등록 방지)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "UpdateWorker", // 유니크한 이름
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicRequest // 실행할 Worker
        )
        Timber.d("WorkManager: enqueueUniquePeriodicWork time : ${getCurrentTimeInHoursAndMinutes()}")

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