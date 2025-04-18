package com.yourssu.soomsil.usaint.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

data class UserPreferences(
    val includeSeasonalSemester: Boolean,
    val notificationEnabled: Boolean,
)

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object PreferencesKeys {
        val CHART_INCLUDE_SEASONAL_SEMESTER =
            booleanPreferencesKey("chart_include_seasonal_semester")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { e ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (e is IOException) {
                Timber.e("Error reading preferences.", e)
                emit(emptyPreferences())
            } else {
                throw e
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    suspend fun updateChartIncludeSeasonal(enable: Boolean) {
        dataStore.edit { preferences ->
            preferences[CHART_INCLUDE_SEASONAL_SEMESTER] = enable
        }
    }

    suspend fun updateNotificationEnabled(enable: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED] = enable
        }
    }

    suspend fun deleteAll() {
        dataStore.edit { preferences ->
            preferences.remove(CHART_INCLUDE_SEASONAL_SEMESTER)
            preferences.remove(NOTIFICATION_ENABLED)
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(
            includeSeasonalSemester = preferences[CHART_INCLUDE_SEASONAL_SEMESTER] ?: false,
            notificationEnabled = preferences[NOTIFICATION_ENABLED] ?: false,
        )
    }
}