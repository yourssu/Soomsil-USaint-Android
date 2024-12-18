package com.yourssu.soomsil.usaint.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.PreferencesKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import dev.eatsteak.rusaint.ffi.USaintSessionBuilder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _uiEvent: MutableSharedFlow<LoginUiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var studentId: String by mutableStateOf("")
    var studentPw: String by mutableStateOf("")

    fun login() {
        viewModelScope.launch {
            _uiEvent.emit(LoginUiEvent.Loading)
            try {
                // 로그인 시도
                USaintSessionBuilder().withPassword(studentId, studentPw)
                _uiEvent.emit(LoginUiEvent.Success)
            } catch (e: RusaintException) {
                _uiEvent.emit(LoginUiEvent.Error("로그인에 실패했습니다. 다시 시도해주세요."))
                return@launch
            }
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.STUDENT_ID] = studentId
                preferences[PreferencesKeys.STUDENT_PW] = studentPw
                // TODO: save student information
            }
        }
    }
}