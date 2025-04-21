package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentInformationDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentCredentialRepository @Inject constructor(
    private val studentInformation: StudentInformationDataSource,
) {
    val studentCredential: Flow<StudentCredential> = studentInformation.studentCredential

    suspend fun setStudentCredential(credential: StudentCredential) =
        studentInformation.setStudentCredential(credential)
}