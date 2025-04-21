package com.yourssu.soomsil.usaint.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInformationSerializer
import com.yourssu.soomsil.usaint.data.source.local.datastore.UserPreferencesSerializer
import com.yourssu.soomsil.usaint.proto.StudentInformation
import com.yourssu.soomsil.usaint.proto.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val STUDENT_INFO = "student_info"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.preferencesDataStoreFile(STUDENT_INFO) }
        )
    }

    @Singleton
    @Provides
    fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<UserPreferences> {
        return DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            produceFile = { context.dataStoreFile("user_preferences.pb") }
        )
    }

    @Singleton
    @Provides
    fun provideStudentInformationDataStore(@ApplicationContext context: Context): DataStore<StudentInformation> {
        return DataStoreFactory.create(
            serializer = StudentInformationSerializer,
            produceFile = { context.dataStoreFile("student_information.pb") }
        )
    }
}