package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.UserData
import com.yourssu.soomsil.usaint.data.source.local.datastore.UserPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
) {
    val userData: Flow<UserData> = userPreferencesDataSource.userData

    suspend fun setNotificationEnabled(enable: Boolean) =
        userPreferencesDataSource.setNotificationEnabled(enable)

    suspend fun setIncludeSeasonalSemester(include: Boolean) =
        userPreferencesDataSource.setIncludeSeasonalSemester(include)
}