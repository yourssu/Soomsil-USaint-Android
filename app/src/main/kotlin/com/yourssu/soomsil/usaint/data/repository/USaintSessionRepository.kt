package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInfoDataStore
import dev.eatsteak.rusaint.ffi.USaintSession
import dev.eatsteak.rusaint.ffi.USaintSessionBuilder
import javax.inject.Inject

class USaintSessionRepository @Inject constructor(
    private val studentInfoDataStore: StudentInfoDataStore,
) {
    suspend fun withPassword(id: String, pw: String): Result<USaintSession> {
        return kotlin.runCatching {
            USaintSessionBuilder().withPassword(id, pw)
        }
    }

    suspend fun getSession(): Result<USaintSession> {
        val (id, pw) = studentInfoDataStore.getPassword().getOrElse { e ->
            return Result.failure(e)
        }
        return kotlin.runCatching {
            USaintSessionBuilder().withPassword(id, pw)
        }
    }
}