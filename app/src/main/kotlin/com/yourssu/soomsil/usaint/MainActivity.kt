package com.yourssu.soomsil.usaint

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.soomsil.usaint.screen.home.navigation.Home
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import com.yourssu.soomsil.usaint.screen.navigation.USaintNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserInfo(
    val id: String? = null,
    val pw: String? = null,
)

val Context.dataStore by preferencesDataStore("student_info")

val STUDENT_ID = stringPreferencesKey("student_id")
val STUDENT_PW = stringPreferencesKey("student_pw")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ReportCardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        val userInfoFlow: Flow<UserInfo> = dataStore.data.map { preferences ->
            val id = preferences[STUDENT_ID]
            val pw = preferences[STUDENT_PW]
            UserInfo(id, pw)
        }

        setContent {
            var startDestination: Any? by remember { mutableStateOf(null) }

            LaunchedEffect(Unit) {
                val userInfo = userInfoFlow.first()
                startDestination = if (userInfo.id == null || userInfo.pw == null) {
                    Login
                } else {
                    Home
                }
            }

            YdsTheme(
                isDarkMode = false
            ) {
                startDestination?.let {
                    USaintNavHost(
                        navController = rememberNavController(),
                        startDestination = it
                    )
                }
            }
        }
    }
}