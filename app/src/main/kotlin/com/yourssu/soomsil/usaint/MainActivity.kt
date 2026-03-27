package com.yourssu.soomsil.usaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import com.yourssu.soomsil.usaint.data.analytics.PosthogTracker
import com.yourssu.soomsil.usaint.screen.home.navigation.Home
import com.yourssu.soomsil.usaint.screen.login.navigation.Login
import com.yourssu.soomsil.usaint.ui.USaintApp
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var posthogTracker: PosthogTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val config = PostHogAndroidConfig(
            apiKey = if (BuildConfig.DEBUG) { BuildConfig.POSTHOG_TOKEN } else { BuildConfig.POSTHOG_TOKEN } ,
            host = "https://us.i.posthog.com"
        )

        PostHogAndroid.setup(this, config)

        setContent {
            val mainUiState by viewModel.mainUiState.collectAsStateWithLifecycle()
            SoomsilUSaintTheme {
                if (mainUiState is MainUiState.Loading) {
                    // TODO loading or splash
                } else {
                    val credentialExist = mainUiState is MainUiState.Success
                    USaintApp(
                        startDestination = if (credentialExist) Home else Login,
                        posthogTracker = posthogTracker,
                    )
                }
            }
        }
    }
}