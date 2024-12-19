package com.yourssu.soomsil.usaint.data.source.remote.rusaint

import dev.eatsteak.rusaint.core.StudentInformation
import dev.eatsteak.rusaint.ffi.StudentInformationApplicationBuilder
import dev.eatsteak.rusaint.ffi.USaintSession
import dev.eatsteak.rusaint.ffi.USaintSessionBuilder
import javax.inject.Inject

class RusaintApi @Inject constructor() {
    suspend fun getUSaintSession(id: String, pw: String): Result<USaintSession> {
        return kotlin.runCatching {
            USaintSessionBuilder().withPassword(id, pw)
        }
    }

    suspend fun getStudentInformation(session: USaintSession): Result<StudentInformation> {
        return kotlin.runCatching {
            StudentInformationApplicationBuilder().build(session).general()
        }
    }
}