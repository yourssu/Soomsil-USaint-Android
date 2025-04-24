package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import javax.inject.Inject

class USaintSessionRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val rusaintApi: RusaintApi,
) {
    suspend fun getSession(credential: StudentCredential) {
        rusaintApi.getUSaintSession(credential)
    }
//    suspend fun getSession(): Result<USaintSession> {
//        val credential = studentCredential.studentCredential.first()
//        return rusaintApi.getUSaintSession(credential)
//    }
}