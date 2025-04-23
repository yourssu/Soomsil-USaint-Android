package com.yourssu.soomsil.usaint.screen.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.LectureRepository
import com.yourssu.soomsil.usaint.data.repository.SemesterRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.data.repository.UserDataRepository
import com.yourssu.soomsil.usaint.domain.usecase.UpdateWorkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val studentDataRepository: StudentDataRepository,
    private val semesterRepository: SemesterRepository,
    private val lectureRepository: LectureRepository,
    private val userDataRepository: UserDataRepository,
    private val updateWorkerUseCase: UpdateWorkerUseCase,
) : ViewModel() {
    val uiState: StateFlow<SettingUiState> =
        userDataRepository.userData
            .map {
                SettingUiState.UserEditableSettings(
                    notificationEnabled = it.notificationEnabled,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SettingUiState.Loading,
            )

    private val _uiEvent = MutableSharedFlow<SettingUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    var showDialog: Boolean by mutableStateOf(false)

    fun updateNotificationSetting(enable: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNotificationEnabled(enable)
            if (enable) {
                updateWorkerUseCase.enqueue()
            } else {
                updateWorkerUseCase.dequeue()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // 하위의 데이터부터 차례로 지우는 것이 좋음
            lectureRepository.deleteAllLectures().onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(SettingUiEvent.FailureLogout)
                return@launch
            }
            semesterRepository.deleteAllSemester().onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(SettingUiEvent.FailureLogout)
                return@launch
            }
//            studentInfoRepository.deleteStudentInfo().onFailure { e ->
//                Timber.e(e)
//                _uiEvent.emit(SettingUiEvent.FailureLogout)
//                return@launch
//            }
//            userDataRepository.deleteAll()
            _uiEvent.emit(SettingUiEvent.SuccessLogout)
        }
    }
}

sealed interface SettingUiState {
    data object Loading : SettingUiState

    data class UserEditableSettings(
        val notificationEnabled: Boolean,
    ) : SettingUiState
}

sealed interface SettingUiEvent {
    data object SuccessLogout : SettingUiEvent
    data object FailureLogout : SettingUiEvent
}
