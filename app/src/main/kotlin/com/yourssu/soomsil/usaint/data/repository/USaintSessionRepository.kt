package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInfoDataStore
import com.yourssu.soomsil.usaint.data.source.remote.rusaint.RusaintApi
import dev.eatsteak.rusaint.ffi.USaintSession
import javax.inject.Inject

class USaintSessionRepository @Inject constructor(
    private val studentInfoDataStore: StudentInfoDataStore,
    private val rusaintApi: RusaintApi,
) {
    suspend fun withPassword(id: String, pw: String): Result<USaintSession> {
        return rusaintApi.getUSaintSession(id, pw)
    }

    suspend fun getSession(): Result<USaintSession> {
        val (id, pw) = studentInfoDataStore.getPassword().getOrElse { e ->
            return Result.failure(e)
        }
        return rusaintApi.getUSaintSession(id, pw)
    }
}