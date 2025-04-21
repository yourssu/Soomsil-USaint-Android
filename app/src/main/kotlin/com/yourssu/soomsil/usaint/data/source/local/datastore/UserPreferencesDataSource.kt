package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import com.yourssu.soomsil.usaint.core.model.UserData
import com.yourssu.soomsil.usaint.proto.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val userData: Flow<UserData> = userPreferences.data
        .map {
            UserData(
                notificationEnabled = it.notificationEnabled,
                includeSeasonalSemester = it.includeSeasonalSemester,
            )
        }

    suspend fun setNotificationEnabled(enable: Boolean) {
        try {
            userPreferences.updateData {
                UserPreferences.newBuilder()
                    .setNotificationEnabled(enable)
                    .setIncludeSeasonalSemester(it.includeSeasonalSemester)
                    .build()
            }
        } catch (e: IOException) {
            Timber.e("Failed to update user preferences", e)
        }
    }

    suspend fun setIncludeSeasonalSemester(include: Boolean) {
        try {
            userPreferences.updateData {
                UserPreferences.newBuilder()
                    .setNotificationEnabled(it.notificationEnabled)
                    .setIncludeSeasonalSemester(include)
                    .build()
            }
        } catch (e: IOException) {
            Timber.e("Failed to update user preferences", e)
        }
    }
}