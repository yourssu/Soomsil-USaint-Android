package com.yourssu.soomsil.usaint

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import com.yourssu.soomsil.usaint.data.analytics.PostHogTracker
import com.yourssu.soomsil.usaint.domain.usecase.UpdateWorkerUseCase
import dagger.hilt.android.HiltAndroidApp
import dev.eatsteak.rusaint.core.RusaintAndroid
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SoomsilUSaintApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var updateWorkerUseCase: UpdateWorkerUseCase

    @Inject
    lateinit var posthogTracker: PostHogTracker

    override fun onCreate() {
        super.onCreate()
        RusaintAndroid.initialize(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val config = PostHogAndroidConfig(
            apiKey = if (BuildConfig.DEBUG) { BuildConfig.POSTHOG_DEV_TOKEN } else { BuildConfig.POSTHOG_TOKEN } ,
            host = "https://us.i.posthog.com"
        )

        PostHogAndroid.setup(this, config)

        updateWorkerUseCase.enqueue()
        posthogTracker.trackAppLaunch()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
