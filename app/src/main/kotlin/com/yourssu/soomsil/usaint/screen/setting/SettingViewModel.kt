package com.yourssu.soomsil.usaint.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.LectureRepository
import com.yourssu.soomsil.usaint.data.repository.SemesterRepository
import com.yourssu.soomsil.usaint.data.repository.StudentInfoRepository
import com.yourssu.soomsil.usaint.data.repository.TotalReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.UserPreferences
import com.yourssu.soomsil.usaint.data.repository.UserPreferencesRepository
import com.yourssu.soomsil.usaint.domain.usecase.UpdateWorkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SettingState(
    val showDialog: Boolean = false,
    val notificationEnabled: Boolean = false,
)

sealed class SettingEvent {
    data object SuccessLogout : SettingEvent()
    data object FailureLogout : SettingEvent()
    data class ClickToggle(val msg: String) : SettingEvent()
}

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val studentInfoRepository: StudentInfoRepository,
    private val totalReportCardRepository: TotalReportCardRepository,
    private val semesterRepository: SemesterRepository,
    private val lectureRepository: LectureRepository,
    private val userPreferencesRepo: UserPreferencesRepository,
    private val updateWorkerUseCase: UpdateWorkerUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingState())
    val uiState: StateFlow<SettingState> = combine(
        _uiState,
        userPreferencesRepo.userPreferencesFlow,
    ) { settingState: SettingState, userPreferences: UserPreferences ->
        settingState.copy(notificationEnabled = userPreferences.notificationEnabled)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingState(),
    )

    private val _uiEvent = MutableSharedFlow<SettingEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updateDialogState(showDialog: Boolean) {
        _uiState.update { it.copy(showDialog = showDialog) }
    }

    fun updateNotificationSetting(enable: Boolean) {
        viewModelScope.launch {
            userPreferencesRepo.updateNotificationEnabled(enable)
            if (enable) {
                _uiEvent.emit(SettingEvent.ClickToggle("알림이 켜졌습니다."))
                updateWorkerUseCase.enqueue()
            } else {
                _uiEvent.emit(SettingEvent.ClickToggle("알림이 꺼졌습니다."))
                updateWorkerUseCase.dequeue()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // 하위의 데이터부터 차례로 지우는 것이 좋음
            lectureRepository.deleteAllLectures().onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(SettingEvent.FailureLogout)
                return@launch
            }
            semesterRepository.deleteAllSemester().onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(SettingEvent.FailureLogout)
                return@launch
            }
            totalReportCardRepository.deleteTotalReportCard().onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(SettingEvent.FailureLogout)
                return@launch
            }
            studentInfoRepository.deleteStudentInfo().onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(SettingEvent.FailureLogout)
                return@launch
            }
            userPreferencesRepo.deleteAll()
            _uiEvent.emit(SettingEvent.SuccessLogout)
        }
    }
}

