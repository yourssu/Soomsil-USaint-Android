package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val STUDENT_ID = stringPreferencesKey("student_id")
    val STUDENT_PW = stringPreferencesKey("student_pw")
    val STUDENT_NAME = stringPreferencesKey("student_name")
    val STUDENT_DEPARTMENT = stringPreferencesKey("student_department")
    val STUDENT_MAJOR = stringPreferencesKey("student_major")
    val STUDENT_GRADE = intPreferencesKey("student_grade")
    val STUDENT_TERM = intPreferencesKey("student_term")
}