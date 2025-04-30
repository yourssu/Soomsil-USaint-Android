package com.yourssu.soomsil.usaint.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.data.repository.StudentCredentialRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.data.repository.UserDataRepository
import com.yourssu.soomsil.usaint.domain.usecase.UpdateWorkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val studentDataRepository: StudentDataRepository,
    private val studentCredentialRepository: StudentCredentialRepository,
    private val userDataRepository: UserDataRepository,
    private val updateWorkerUseCase: UpdateWorkerUseCase,
) : ViewModel() {
    private val _uiEvent: MutableSharedFlow<LoginUiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var isLoading: Boolean by mutableStateOf(false)
        private set
    var studentId: String by mutableStateOf("")
    var studentPw: String by mutableStateOf("")

    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNotificationEnabled(enabled)
        }
        if (enabled) {
            // WorkManager 등록
            updateWorkerUseCase.enqueue()
        }
    }

    fun login() {
        val credential = StudentCredential(id = studentId, password = studentPw)

        viewModelScope.launch {
            isLoading = true
            // id/pw 저장
            studentCredentialRepository.setStudentCredential(credential)
            // 저장된 id/pw로 학생 데이터 가져오기 시도
            studentDataRepository.fetchStudentData()
                .onSuccess {
                    studentCredentialRepository.setLoggedIn(true)
                    _uiEvent.emit(LoginUiEvent.Success)
                }
                .onFailure { e ->
                    _uiEvent.emit(LoginUiEvent.Failure(e.message))
                }
            isLoading = false
        }
    }
}

sealed interface LoginUiEvent {
    data object Success : LoginUiEvent
    data class Failure(val message: String?) : LoginUiEvent
}
