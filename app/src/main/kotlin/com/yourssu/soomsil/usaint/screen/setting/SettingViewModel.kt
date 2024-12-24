package com.yourssu.soomsil.usaint.screen.setting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.LectureRepository
import com.yourssu.soomsil.usaint.data.repository.SemesterRepository
import com.yourssu.soomsil.usaint.data.repository.StudentInfoRepository
import com.yourssu.soomsil.usaint.data.repository.TotalReportCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SettingState(
    val showDialog: Boolean = false,
    val checkAlarm: Boolean = false,
)

sealed class SettingEvent {
    data class SuccessLogout(val msg: String) : SettingEvent()
    data class FailureLogout(val msg: String) : SettingEvent()
    object ShowPermissionRequest : SettingEvent()
    object NavigateToSettings : SettingEvent()
    data class ClickToggle(val msg: String) : SettingEvent()
}

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val studentInfoRepository: StudentInfoRepository,
    private val totalReportCardRepository: TotalReportCardRepository,
    private val semesterRepository: SemesterRepository,
    private val lectureRepository: LectureRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SettingEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updateDialogState(showDialog: Boolean) {
        _state.value = _state.value.copy(showDialog = showDialog)
    }

    fun updateAlarmState(checkAlarm: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(checkAlarm = checkAlarm)
            if (checkAlarm) {
                _uiEvent.emit(SettingEvent.ClickToggle("알림이 켜졌습니다."))
            } else {
                _uiEvent.emit(SettingEvent.ClickToggle("알림이 꺼졌습니다."))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // 하위의 데이터부터 차례로 지우는 것이 좋음
            lectureRepository.deleteAllLectures().onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(SettingEvent.FailureLogout("로그아웃을 다시 시도해주세요."))
                return@launch
            }
            semesterRepository.deleteAllSemester().onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(SettingEvent.FailureLogout("로그아웃을 다시 시도해주세요."))
                return@launch
            }
            totalReportCardRepository.deleteTotalReportCard().onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(SettingEvent.FailureLogout("로그아웃을 다시 시도해주세요."))
                return@launch
            }
            studentInfoRepository.deleteStudentInfo().onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(SettingEvent.FailureLogout("로그아웃을 다시 시도해주세요."))
                return@launch
            }
            _uiEvent.emit(SettingEvent.SuccessLogout("로그아웃 되었습니다."))
        }
    }

    fun checkNotificationPermission(context: Context, isChecked: Boolean) {
        if (isChecked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        updateAlarmState(true)
                    }

                    shouldShowRequestPermissionRationale(
                        context as ComponentActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) -> {
                        viewModelScope.launch {
                            _uiEvent.emit(SettingEvent.ShowPermissionRequest)
                        }
                    }

                    else -> {
                        viewModelScope.launch {
                            _uiEvent.emit(SettingEvent.NavigateToSettings)
                        }
                    }
                }
            } else {
                updateAlarmState(true) // Android 13 미만은 권한 요청이 불필요
            }
        } else {
            updateAlarmState(false)
        }
    }

    fun handlePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            updateAlarmState(true)
        } else {
            updateAlarmState(false)
        }
    }
}

