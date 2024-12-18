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
import com.yourssu.soomsil.usaint.data.repository.StudentInfoRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val uSaintSessionRepo: USaintSessionRepository,
    private val studentInfoRepo: StudentInfoRepository,
) : ViewModel() {
    private val _uiEvent: MutableSharedFlow<LoginUiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var studentId: String by mutableStateOf("")
    var studentPw: String by mutableStateOf("")

    fun login() {
        viewModelScope.launch {
            _uiEvent.emit(LoginUiEvent.Loading)
            // 로그인 시도
            // 실패 시 Error 이벤트 발생 후 종료
            val session = uSaintSessionRepo.withPassword(studentId, studentPw).getOrElse { e ->
                Timber.e(e)
                val errMsg = when (e) {
                    is RusaintException -> "로그인에 실패했습니다. 다시 시도해주세요."
                    else -> "알 수 없는 문제가 발생했습니다."
                }
                _uiEvent.emit(LoginUiEvent.Error(errMsg))
                return@launch
            }
            val studentInfo = studentInfoRepo.getStudentInfo(session).getOrElse { e ->
                Timber.e(e)
                _uiEvent.emit(LoginUiEvent.Error("학생 정보를 불러오는 데 실패했습니다."))
                return@launch
            }
            // 성공 시 id/pw, 학생 정보 저장
            dataStore.edit { pref ->
                pref[PreferencesKeys.STUDENT_ID] = studentId
                pref[PreferencesKeys.STUDENT_PW] = studentPw
                pref[PreferencesKeys.STUDENT_NAME] = studentInfo.name
                pref[PreferencesKeys.STUDENT_DEPARTMENT] = studentInfo.department
                pref[PreferencesKeys.STUDENT_MAJOR] = studentInfo.major ?: ""
                pref[PreferencesKeys.STUDENT_GRADE] = studentInfo.grade.toInt()
                pref[PreferencesKeys.STUDENT_TERM] = studentInfo.term.toInt()
            }
            _uiEvent.emit(LoginUiEvent.Success)
        }
    }
}