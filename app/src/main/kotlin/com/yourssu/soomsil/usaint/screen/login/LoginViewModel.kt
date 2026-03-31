package com.yourssu.soomsil.usaint.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.StudentCredential
import com.yourssu.soomsil.usaint.data.analytics.PostHogTracker
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.StudentCredentialRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.data.repository.UserDataRepository
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val studentDataRepository: StudentDataRepository,
    private val studentCredentialRepository: StudentCredentialRepository,
    private val userDataRepository: UserDataRepository,
    private val chapelRepository: ChapelRepository,
    private val getCurrentSemesterUseCase: GetCurrentSemesterUseCase,
    private val posthogTracker: PostHogTracker
//    private val updateWorkerUseCase: UpdateWorkerUseCase,
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
//        if (enabled) {
//            // WorkManager 등록
//            updateWorkerUseCase.enqueue()
//        }
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
                    getCurrentSemesterUseCase()?.let { // 현재 학기 채플 정보 불러오기
                        chapelRepository.fetchChapelCardData(it)
                            .onFailure {
                                // 로그인 실패 여부는 fetchStudentData()에서 한번에 판단
                                    e -> Timber.e(e)
                            }
                    }

                    studentCredentialRepository.setLoggedIn(true)
                    _uiEvent.emit(LoginUiEvent.Success)
                    posthogTracker.trackLogin(credential.id)
                }
                .onFailure { e ->
//                    posthogTracker.trackLoginFailed(e)
//                    _uiEvent.emit(LoginUiEvent.Failure(e.message))

                    // TODO RUSAINT API 수정 이후 밑 구문을 삭제해주세요. 성적 정보를 못불러오는 경우가 있습니다.
                    // 성적 정보 가져오기에 실패한 경우 채플 정보만 불러옴
                    getCurrentSemesterUseCase()?.let { // 현재 학기 채플 정보 불러오기
                        chapelRepository.fetchChapelCardData(it)
                            .onSuccess {
                                studentCredentialRepository.setLoggedIn(true)
                                _uiEvent.emit(LoginUiEvent.Success)
                                posthogTracker.trackLogin(credential.id)
                            }
                            .onFailure { e ->
                                // 로그인 실패 여부는 fetchStudentData()에서 한번에 판단
                                posthogTracker.trackLoginFailed(e)
                                _uiEvent.emit(LoginUiEvent.Failure(e.message))
                                Timber.e(e)
                            }
                    }
                }

            isLoading = false
        }
    }
}

sealed interface LoginUiEvent {
    data object Success : LoginUiEvent
    data class Failure(val message: String?) : LoginUiEvent
}
