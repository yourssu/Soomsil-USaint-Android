package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.proto.StudentInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class StudentInformationDataSource @Inject constructor(
    private val studentInformation: DataStore<StudentInformation>,
) {
    val studentData: Flow<StudentData> = studentInformation.data
        .map {
            StudentData(
                id = it.id,
                name = it.name,
                grade = it.grade,
                semester = it.semester,
                status = it.status,
                applyYear = it.applyYear,
                applyType = it.applyType,
                department = it.department,
                majors = it.majorsList.toList(),
            )
        }

    suspend fun setStudentData(studentData: StudentData) {
        try {
            studentInformation.updateData {
                it.copy {
                    setId(studentData.id)
                    setName(studentData.name)
                    setGrade(studentData.grade)
                    setSemester(studentData.semester)
                    setStatus(studentData.status)
                    setApplyYear(studentData.applyYear)
                    setApplyType(studentData.applyType)
                    setDepartment(studentData.department)
                    clearMajors()
                    addAllMajors(studentData.majors)
                }
            }
        } catch (e: IOException) {
            Timber.e("Failed to update student data", e)
        }
    }

    suspend fun clear() {
        try {
            studentInformation.updateData { StudentInformation.getDefaultInstance() }
        } catch (e: IOException) {
            Timber.e("Failed to clear student data", e)
        }
    }
}

private fun StudentInformation.copy(builder: StudentInformation.Builder.() -> Unit) =
    StudentInformation.newBuilder(this).apply(builder).build()
