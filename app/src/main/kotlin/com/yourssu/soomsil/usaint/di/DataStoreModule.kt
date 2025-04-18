package com.yourssu.soomsil.usaint.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val STUDENT_INFO = "student_info"
private const val STUDENT_INFO_DATA_STORE_FILE_NAME = "student_info.pb"

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

//    @Singleton
//    @Provides
//    fun provideStudentInformationDataStore(@ApplicationContext context: Context): DataStore<StudentInformation> {
//        return DataStoreFactory.create(
//            serializer = StudentInformationSerializer,
//            produceFile = { context.dataStoreFile(STUDENT_INFO_DATA_STORE_FILE_NAME) }
//        )
//    }
}