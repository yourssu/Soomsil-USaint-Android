package com.yourssu.soomsil.usaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.screen.home.navigation.Home
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import com.yourssu.soomsil.usaint.ui.USaintApp
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val mainUiState by viewModel.mainUiState.collectAsStateWithLifecycle()
            SoomsilUSaintTheme {
                if (mainUiState is MainUiState.Loading) {
                    // TODO loading or splash
                } else {
                    val credentialExist = mainUiState is MainUiState.Success
                    USaintApp(
                        startDestination = if (credentialExist) Home else Login,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}