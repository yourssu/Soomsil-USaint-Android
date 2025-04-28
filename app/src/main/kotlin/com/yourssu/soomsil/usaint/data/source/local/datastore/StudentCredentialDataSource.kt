package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.proto.StudentCredentialProto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class StudentCredentialDataSource @Inject constructor(
    private val studentCredentialDataStore: DataStore<StudentCredentialProto>,
) {
    internal suspend fun getStudentCredential(): StudentCredential = studentCredentialDataStore.data
        .map {
            StudentCredential(
                id = it.id,
                password = it.password,
            )
        }.first()

    suspend fun getLoggedIn(): Boolean =
        studentCredentialDataStore.data.map { it.isLoggedIn }.first()

    suspend fun setLoggedIn(login: Boolean) {
        try {
            studentCredentialDataStore.updateData {
                it.copy {
                    setIsLoggedIn(login)
                }
            }
        } catch (e: IOException) {
            Timber.e("Failed to update logged in", e)
        }
    }

    suspend fun setStudentCredential(credential: StudentCredential) {
        try {
            studentCredentialDataStore.updateData {
                it.copy {
                    setId(credential.id)
                    setPassword(credential.password)
                }
            }
        } catch (e: IOException) {
            Timber.e("Failed to update student credential", e)
        }
    }
}

private fun StudentCredentialProto.copy(builder: StudentCredentialProto.Builder.() -> Unit) =
    StudentCredentialProto.newBuilder().apply(builder).build()