package com.yourssu.soomsil.usaint

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import com.yourssu.soomsil.usaint.screen.navigation.USaintNavHost
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.eatsteak.rusaint.core.CourseType
import dev.eatsteak.rusaint.core.SemesterType
import dev.eatsteak.rusaint.ffi.CourseGradesApplicationBuilder
import dev.eatsteak.rusaint.ffi.StudentInformationApplicationBuilder
import dev.eatsteak.rusaint.ffi.USaintSessionBuilder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ReportCardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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
            SoomsilUSaintTheme {
                YdsTheme {
                    YdsScaffold(
                        modifier = Modifier
                            .systemBarsPadding()
                            .statusBarsPadding()
                    ) {
                        USaintNavHost(
                            navController = navController,
                            startDestination = Login
                        )
                    }
                }
            }
        }
    }
}