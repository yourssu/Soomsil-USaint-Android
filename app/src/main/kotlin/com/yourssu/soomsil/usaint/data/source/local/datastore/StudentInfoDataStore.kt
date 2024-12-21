package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.yourssu.soomsil.usaint.PreferencesKeys
import com.yourssu.soomsil.usaint.data.model.StudentInfoDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StudentInfoDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun getPassword(): Result<Pair<String, String>> {
        return kotlin.runCatching {
            dataStore.data.map { pref ->
                Pair(
                    pref[PreferencesKeys.STUDENT_ID] ?: throw Exception("id not found"),
                    pref[PreferencesKeys.STUDENT_PW] ?: throw Exception("pw not found"),
                )
            }.first()
        }
    }

    suspend fun setPassword(id: String, pw: String): Result<Unit> {
        return kotlin.runCatching {
            dataStore.edit { pref ->
                pref[PreferencesKeys.STUDENT_ID] = id
                pref[PreferencesKeys.STUDENT_PW] = pw
            }
        }
    }

    suspend fun getStudentInfo(): Result<StudentInfoDto> {
        return kotlin.runCatching {
            dataStore.data.map { pref ->
                StudentInfoDto(
                    name = pref[PreferencesKeys.STUDENT_NAME]
                        ?: throw Exception("name not found"),
                    department = pref[PreferencesKeys.STUDENT_DEPARTMENT]
                        ?: throw Exception("department not found"),
                    major = pref[PreferencesKeys.STUDENT_MAJOR],
                    grade = pref[PreferencesKeys.STUDENT_GRADE]?.toUInt()
                        ?: throw Exception("grade not found"),
                    term = pref[PreferencesKeys.STUDENT_TERM]?.toUInt()
                        ?: throw Exception("term not found"),
                )
            }.first()
        }
    }

    suspend fun setStudentInfo(studentInfo: StudentInfoDto): Result<Unit> {
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

    suspend fun deleteStudentInfo(): Result<Unit> {
        return kotlin.runCatching {
            dataStore.edit { pref ->
                pref.remove(PreferencesKeys.STUDENT_ID)
                pref.remove(PreferencesKeys.STUDENT_PW)
                pref.remove(PreferencesKeys.STUDENT_NAME)
                pref.remove(PreferencesKeys.STUDENT_DEPARTMENT)
                pref.remove(PreferencesKeys.STUDENT_MAJOR)
                pref.remove(PreferencesKeys.STUDENT_GRADE)
                pref.remove(PreferencesKeys.STUDENT_TERM)
            }
        }
    }
}