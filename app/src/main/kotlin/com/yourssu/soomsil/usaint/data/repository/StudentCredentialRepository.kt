package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import javax.inject.Inject

class StudentCredentialRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
) {
    suspend fun getStudentCredential(): StudentCredential =
        studentCredential.getStudentCredential()

    suspend fun setStudentCredential(credential: StudentCredential) =
        studentCredential.setStudentCredential(credential)
}