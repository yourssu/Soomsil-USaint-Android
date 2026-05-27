package com.yourssu.soomsil.usaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yourssu.soomsil.usaint.data.analytics.MixpanelTracker
import com.yourssu.soomsil.usaint.screen.main.navigation.Main
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import com.yourssu.soomsil.usaint.ui.USaintApp
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var mixpanelTracker: MixpanelTracker

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
                        startDestination = if (credentialExist) Main else Login,
                        mixpanelTracker = mixpanelTracker,
                    )
                }
            }
        }
    }
}