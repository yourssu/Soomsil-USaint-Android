package com.yourssu.soomsil.usaint.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object PreferencesKeys {
        val CHART_INCLUDE_SEASONAL_SEMESTER =
            booleanPreferencesKey("chart_include_seasonal_semester")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    }

    val includeSeasonalSemesterFlow: Flow<Boolean> = dataStore.data
        .map { pref -> pref[CHART_INCLUDE_SEASONAL_SEMESTER] ?: false }

    val notificationEnabledFlow: Flow<Boolean> = dataStore.data
        .map { pref -> pref[NOTIFICATION_ENABLED] ?: false }

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
}