package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import com.yourssu.soomsil.usaint.core.model.UserData
import com.yourssu.soomsil.usaint.core.types.SemesterType
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
                isCurrentSemesterSpecified = it.isCurrentSemesterSpecified,
                specifiedCurrentSemester = try {
                    Pair(it.specifiedCurrentYear, enumValueOf(it.specifiedCurrentSemester))
                } catch (e: Exception) {
                    null
                },
                autoFetch = it.autoFetch,
            )
        }

    suspend fun setNotificationEnabled(enable: Boolean) {
        try {
            userPreferences.updateData {
                it.copy { setNotificationEnabled(enable) }
            }
        } catch (e: IOException) {
            Timber.e("Failed to update user preferences", e)
        }
    }

    suspend fun setIncludeSeasonalSemester(include: Boolean) {
        try {
            userPreferences.updateData {
                it.copy { setIncludeSeasonalSemester(include) }
            }
        } catch (e: IOException) {
            Timber.e("Failed to update user preferences", e)
        }
    }

    suspend fun setAutoFetch(autoFetch: Boolean) {
        try {
            userPreferences.updateData {
                it.copy { setAutoFetch(autoFetch) }
            }
        } catch (e: IOException) {
            Timber.e("Failed to update user preferences", e)
        }
    }

    suspend fun setCurrentSemesterSpecified(year: Int, semester: SemesterType) {
        try {
            userPreferences.updateData {
                it.copy {
                    setIsCurrentSemesterSpecified(true)
                    setSpecifiedCurrentYear(year)
                    setSpecifiedCurrentSemester(semester.name)
                }
            }
        } catch (e: IOException) {
            Timber.e("Failed to update user preferences", e)
        }
    }

    suspend fun setCurrentSemesterUnspecified() {
        try {
            userPreferences.updateData {
                it.copy { setIsCurrentSemesterSpecified(false) }
            }
        } catch (e: IOException) {
            Timber.e("Failed to update user preferences", e)
        }
    }

    suspend fun clear() {
        try {
            userPreferences.updateData { UserPreferences.getDefaultInstance() }
        } catch (e: IOException) {
            Timber.e("Failed to clear user preferences", e)
        }
    }
}

private fun UserPreferences.copy(builder: UserPreferences.Builder.() -> Unit) =
    UserPreferences.newBuilder(this).apply(builder).build()
