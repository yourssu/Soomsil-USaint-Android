package com.yourssu.soomsil.usaint.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.yourssu.soomsil.usaint.PreferencesKeys
import dev.eatsteak.rusaint.ffi.USaintSession
import dev.eatsteak.rusaint.ffi.USaintSessionBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class USaintSessionRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun withPassword(id: String, pw: String): Result<USaintSession> {
        return kotlin.runCatching {
            USaintSessionBuilder().withPassword(id, pw)
        }
    }

    suspend fun getSession(): Result<USaintSession> {
        val (id, pw) = dataStore.data.map { pref ->
            Pair(pref[PreferencesKeys.STUDENT_ID], pref[PreferencesKeys.STUDENT_PW])
        }.first()
        if (id == null || pw == null) return Result.failure(Exception("id or pw not found"))
        return kotlin.runCatching {
            USaintSessionBuilder().withPassword(id, pw)
        }
    }
}