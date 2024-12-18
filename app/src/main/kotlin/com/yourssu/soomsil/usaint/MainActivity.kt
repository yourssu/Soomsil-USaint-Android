package com.yourssu.soomsil.usaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import com.yourssu.soomsil.usaint.screen.navigation.USaintNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ReportCardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        setContent {
            val navController: NavHostController = rememberNavController()

            LaunchedEffect(Unit) {

//                viewModel.testOperations()
                viewModel.testDuplicateHandling()

//                val session = USaintSessionBuilder().withPassword("20211722", "kwakkun1208!")
//                val courseGradesApplication = CourseGradesApplicationBuilder().build(session)
//                val studentInformationApplication = StudentInformationApplicationBuilder().build(session)

//                Log.d("MainActivity", courseGradesApplication.semesters(CourseType.BACHELOR).toString())
//                Log.d(
//                    "MainActivity",
//                    courseGradesApplication.classes(CourseType.BACHELOR, "2021", SemesterType.TWO, true)
//                        .toString()
//                )
//                Log.d("MainActivity", studentInformationApplication.general().toString())
//                Log.d("MainActivity", studentInformationApplication.family().toString())
//                Log.d("MainActivity", studentInformationApplication.work().toString())
            }
            YdsTheme(
                isDarkMode = false
            ) {
                YdsScaffold {
                    USaintNavHost(
                        navController = navController,
                        startDestination = Login
                    )
                }
            }
        }
    }
}