package com.yourssu.soomsil.usaint.di

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.yourssu.soomsil.usaint.BuildConfig
import com.yourssu.soomsil.usaint.data.analytics.MixpanelTracker
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideMixpanel(@ApplicationContext context: Context): MixpanelAPI {
        val token = BuildConfig.MIXPANEL_TOKEN
        return MixpanelAPI.getInstance(context, token, false)
    }

    @Provides
    @Singleton
    fun provideMixpanelTracker(
        mixpanel: MixpanelAPI,
        studentDataRepository: StudentDataRepository,
        chapelRepository: ChapelRepository,
    ): MixpanelTracker = MixpanelTracker(mixpanel, studentDataRepository, chapelRepository)

}
