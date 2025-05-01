package com.yourssu.soomsil.usaint.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.yourssu.soomsil.usaint.data.source.local.datastore.ReportCardSummarySerializer
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialSerializer
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInformationSerializer
import com.yourssu.soomsil.usaint.data.source.local.datastore.UserPreferencesSerializer
import com.yourssu.soomsil.usaint.proto.ReportCardSummaryProto
import com.yourssu.soomsil.usaint.proto.StudentCredentialProto
import com.yourssu.soomsil.usaint.proto.StudentInformation
import com.yourssu.soomsil.usaint.proto.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
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

    @Singleton
    @Provides
    fun provideStudentCredentialDataStore(@ApplicationContext context: Context): DataStore<StudentCredentialProto> {
        return DataStoreFactory.create(
            serializer = StudentCredentialSerializer,
            produceFile = { context.dataStoreFile("student_credential.pb") }
        )
    }

    @Singleton
    @Provides
    fun provideReportCardSummaryDataStore(@ApplicationContext context: Context): DataStore<ReportCardSummaryProto> {
        return DataStoreFactory.create(
            serializer = ReportCardSummarySerializer,
            produceFile = { context.dataStoreFile("report_card_summary.pb") }
        )
    }
}