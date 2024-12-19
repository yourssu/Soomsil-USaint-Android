package com.yourssu.soomsil.usaint.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.yourssu.soomsil.usaint.PreferencesKeys
import com.yourssu.soomsil.usaint.data.model.StudentInfoDto
import dev.eatsteak.rusaint.ffi.StudentInformationApplicationBuilder
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StudentInfoRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>, // fixme: dataStore에서 가져오는 로직은 다른 곳으로 빼는 게 맞을지?
    private val uSaintSessionRepo: USaintSessionRepository,
) {
    suspend fun getStudentInfoFromDataStore(): Result<StudentInfoDto> {
        return kotlin.runCatching {
            dataStore.data.map { pref ->
                StudentInfoDto(
                    name = pref[PreferencesKeys.STUDENT_NAME] ?: throw Exception("not found"),
                    department = pref[PreferencesKeys.STUDENT_DEPARTMENT]
                        ?: throw Exception("not found"),
                    major = pref[PreferencesKeys.STUDENT_MAJOR],
                    grade = pref[PreferencesKeys.STUDENT_GRADE]?.toUInt()
                        ?: throw Exception("not found"),
                    term = pref[PreferencesKeys.STUDENT_TERM]?.toUInt()
                        ?: throw Exception("not found"),
                )
            }.first()
        }
    }

    suspend fun storePassword(id: String, pw: String): Result<Unit> {
        return kotlin.runCatching {
            dataStore.edit { pref ->
                pref[PreferencesKeys.STUDENT_ID] = id
                pref[PreferencesKeys.STUDENT_PW] = pw
            }
        }
    }

    suspend fun storeStudentInfo(studentInfo: StudentInfoDto): Result<Unit> {
        return kotlin.runCatching {
            dataStore.edit { pref ->
                pref[PreferencesKeys.STUDENT_NAME] = studentInfo.name
                pref[PreferencesKeys.STUDENT_DEPARTMENT] = studentInfo.department
                pref[PreferencesKeys.STUDENT_MAJOR] = studentInfo.major ?: ""
                pref[PreferencesKeys.STUDENT_GRADE] = studentInfo.grade.toInt()
                pref[PreferencesKeys.STUDENT_TERM] = studentInfo.term.toInt()
            }
        }
    }

    suspend fun getStudentInfo(session: USaintSession): Result<StudentInfoDto> {
        return kotlin.runCatching {
            val studentInfo = StudentInformationApplicationBuilder().build(session).general()
            StudentInfoDto(
                name = studentInfo.name,
                department = studentInfo.department,
                major = studentInfo.major,
                grade = studentInfo.grade,
                term = studentInfo.term,
            )
        }
    }

    suspend fun getStudentInfo(): Result<StudentInfoDto> {
        val session = uSaintSessionRepo.getSession().getOrElse { e ->
            return Result.failure(e)
        }
        return getStudentInfo(session)
    }
}