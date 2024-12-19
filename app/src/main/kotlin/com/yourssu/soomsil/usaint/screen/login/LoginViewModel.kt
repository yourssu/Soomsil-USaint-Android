package com.yourssu.soomsil.usaint.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val uSaintSessionRepo: USaintSessionRepository,
    private val studentInfoRepo: StudentInfoRepository,
) : ViewModel() {
    private val _uiEvent: MutableSharedFlow<LoginUiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var isLoading: Boolean by mutableStateOf(false)
        private set
    var studentId: String by mutableStateOf("")
    var studentPw: String by mutableStateOf("")

    fun login() {
        val id = studentId
        val pw = studentPw

        viewModelScope.launch {
            isLoading = true
            // 로그인 시도
            // 실패 시 Error 이벤트 발생 후 종료
            val session = uSaintSessionRepo.withPassword(id, pw).getOrElse { e ->
                Timber.e(e)
                val errMsg = when (e) {
                    is RusaintException -> "로그인에 실패했습니다. 다시 시도해주세요."
                    else -> "알 수 없는 문제가 발생했습니다."
                }
                isLoading = false
                _uiEvent.emit(LoginUiEvent.Error(errMsg))
                return@launch
            }
            val studentInfo = studentInfoRepo.getStudentInfo(session).getOrElse { e ->
                Timber.e(e)
                isLoading = false
                _uiEvent.emit(LoginUiEvent.Error("학생 정보를 불러오는 데 실패했습니다."))
                return@launch
            }
            // 성공 시 id/pw, 학생 정보 저장
            studentInfoRepo.storePassword(id, pw).onFailure { e -> Timber.e(e) }
            studentInfoRepo.storeStudentInfo(studentInfo).onFailure { e -> Timber.e(e) }
            isLoading = true
            _uiEvent.emit(LoginUiEvent.Success)
        }
    }
}