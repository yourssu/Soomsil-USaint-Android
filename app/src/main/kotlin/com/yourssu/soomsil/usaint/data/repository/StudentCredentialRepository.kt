package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import javax.inject.Inject

class StudentCredentialRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
) {
    suspend fun isLoggedIn(): Boolean = studentCredential.getLoggedIn()

    suspend fun setLoggedIn(login: Boolean) =
        studentCredential.setLoggedIn(login)

    suspend fun setStudentCredential(credential: StudentCredential) =
        studentCredential.setStudentCredential(credential)
}