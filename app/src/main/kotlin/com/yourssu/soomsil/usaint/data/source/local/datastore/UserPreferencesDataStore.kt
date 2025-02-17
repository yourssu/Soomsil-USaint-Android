package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun getChartIncludeSeasonal(default: Boolean = false): Result<Boolean> {
        return kotlin.runCatching {
            dataStore.data.map { pref ->
                pref[PreferencesKeys.CHART_INCLUDE_SEASONAL_SEMESTER] ?: default
            }.first()
        }
    }

    suspend fun setChartIncludeSeasonal(value: Boolean): Result<Unit> {
        return kotlin.runCatching {
            dataStore.edit { pref ->
                pref[PreferencesKeys.CHART_INCLUDE_SEASONAL_SEMESTER] = value
            }
        }
    }

    suspend fun getSettingNotification(default: Boolean = false): Result<Boolean> {
        return kotlin.runCatching {
            dataStore.data.map { pref ->
                pref[PreferencesKeys.SETTING_NOTIFICATION] ?: default
            }.first()
        }
    }

    suspend fun setSettingNotification(value: Boolean): Result<Unit> {
        return kotlin.runCatching {
            dataStore.edit { pref ->
                pref[PreferencesKeys.SETTING_NOTIFICATION] = value
            }
        }
    }

    suspend fun deleteUserPreferences(): Result<Unit> {
        return kotlin.runCatching {
            dataStore.edit { pref ->
                pref.remove(PreferencesKeys.SETTING_NOTIFICATION)
                pref.remove(PreferencesKeys.CHART_INCLUDE_SEASONAL_SEMESTER)
            }
        }
    }
}