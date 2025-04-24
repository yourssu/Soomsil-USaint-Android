package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.proto.StudentCredentialProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class StudentCredentialDataSource @Inject constructor(
    private val studentCredentialDataStore: DataStore<StudentCredentialProto>,
) {
    val studentCredential: Flow<StudentCredential> = studentCredentialDataStore.data
        .map {
            StudentCredential(
                id = it.id,
                password = it.password,
            )
        }

    suspend fun setStudentCredential(credential: StudentCredential) {
        try {
            studentCredentialDataStore.updateData {
                StudentCredentialProto.newBuilder()
                    .setId(credential.id)
                    .setPassword(credential.password)
                    .build()
            }
        } catch (e: IOException) {
            Timber.e("Failed to update student credential", e)
        }
    }
}