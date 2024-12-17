package com.yourssu.soomsil.usaint

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import dev.eatsteak.rusaint.core.CourseType
import dev.eatsteak.rusaint.core.SemesterType
import dev.eatsteak.rusaint.ffi.CourseGradesApplicationBuilder
import dev.eatsteak.rusaint.ffi.StudentInformationApplicationBuilder
import dev.eatsteak.rusaint.ffi.USaintSessionBuilder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "my_database"
//        ).build()
//
//        val dao = db.gradeDao()
//
//        // Repository 인스턴스 생성
//        val repository = TotalReportCardRepository(
//            totalReportCardDao = dao
//        )
//
//        lifecycleScope.launch {
//            // 데이터베이스에 데이터 삽입
//            repository.insertTotalReportCard(earnedCredit = 100.0f, gpa = 4.5f)
//
//            // 데이터베이스에서 데이터 조회
//            val totalReportCard = repository.getTotalReportCard()
//            Log.d("MainActivity", totalReportCard.toString())
//        }


        enableEdgeToEdge()
        setContent {
            LaunchedEffect(Unit) {
                val session = USaintSessionBuilder().withPassword("20211722", "kwakkun1208!")
                val courseGradesApplication = CourseGradesApplicationBuilder().build(session)
                val studentInformationApplication = StudentInformationApplicationBuilder().build(session)

                Log.d("MainActivity", courseGradesApplication.semesters(CourseType.BACHELOR).toString())
                Log.d(
                    "MainActivity",
                    courseGradesApplication.classes(CourseType.BACHELOR, "2021", SemesterType.ONE, false)
                        .toString()
                )
                Log.d(
                    "MainActivity",
                    courseGradesApplication.classes(CourseType.BACHELOR, "2021", SemesterType.TWO, true)
                        .toString()
                )
                Log.d("MainActivity", studentInformationApplication.general().toString())
                Log.d("MainActivity", studentInformationApplication.family().toString())
                Log.d("MainActivity", studentInformationApplication.work().toString())
            }
            SoomsilUSaintTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SoomsilUSaintTheme {
        Greeting("Android")
    }
}