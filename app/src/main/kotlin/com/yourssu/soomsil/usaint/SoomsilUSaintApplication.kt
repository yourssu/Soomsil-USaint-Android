package com.yourssu.soomsil.usaint

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.yourssu.soomsil.usaint.data.analytics.MixpanelTracker
import com.yourssu.soomsil.usaint.domain.usecase.UpdateWorkerUseCase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SoomsilUSaintApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var updateWorkerUseCase: UpdateWorkerUseCase

    @Inject
    lateinit var mixpanelTracker: MixpanelTracker

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        updateWorkerUseCase.enqueue()
        mixpanelTracker.trackAppLaunch()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
