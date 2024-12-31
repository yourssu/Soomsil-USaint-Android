package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInfoDataStore
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import com.yourssu.soomsil.usaint.domain.type.UserCredential
import dev.eatsteak.rusaint.ffi.USaintSession
import javax.inject.Inject

class USaintSessionRepository @Inject constructor(
    private val studentInfoDataStore: StudentInfoDataStore,
    private val rusaintApi: RusaintApi,
) {
    suspend fun withPassword(userCredential: UserCredential): Result<USaintSession> {
        return rusaintApi.getUSaintSession(userCredential.id, userCredential.pw)
    }

    suspend fun getSession(): Result<USaintSession> {
        val (id, pw) = studentInfoDataStore.getUserCredential().getOrElse { e ->
            return Result.failure(e)
        }
        return rusaintApi.getUSaintSession(id, pw)
    }
}