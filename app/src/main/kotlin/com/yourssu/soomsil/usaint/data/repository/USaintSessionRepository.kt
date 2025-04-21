package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInformationDataSource
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class USaintSessionRepository @Inject constructor(
    private val studentInformation: StudentInformationDataSource,
    private val rusaintApi: RusaintApi,
) {
    suspend fun withPassword(credential: StudentCredential): Result<USaintSession> {
        return rusaintApi.getUSaintSession(credential.id, credential.password)
    }

    suspend fun getSession(): Result<USaintSession> {
        val (id, password) = studentInformation.studentCredential.first()
        return rusaintApi.getUSaintSession(id, password)
    }
}