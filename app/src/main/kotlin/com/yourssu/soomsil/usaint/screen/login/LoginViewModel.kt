package com.yourssu.soomsil.usaint.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.PreferencesKeys.STUDENT_ID
import com.yourssu.soomsil.usaint.PreferencesKeys.STUDENT_PW
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    var studentId: String by mutableStateOf("")
    var studentPw: String by mutableStateOf("")

    fun setIdPw() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[STUDENT_ID] = studentId
                preferences[STUDENT_PW] = studentPw
            }
        }
    }
}