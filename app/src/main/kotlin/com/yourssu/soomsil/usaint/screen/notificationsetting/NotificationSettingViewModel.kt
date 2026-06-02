package com.yourssu.soomsil.usaint.screen.mypage

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NotificationSettingUiState(
    val pushNotificationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = false,
    val courseRegistrationEnabled: Boolean = true,
    val assignmentDeadlineEnabled: Boolean = true,
    val gradeReleaseEnabled: Boolean = true,
    val chapelEnabled: Boolean = true,
    val marketingEnabled: Boolean = false,
)

@HiltViewModel
class NotificationSettingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationSettingUiState())
    val uiState: StateFlow<NotificationSettingUiState> = _uiState.asStateFlow()

    fun setPushNotificationEnabled(enabled: Boolean) =
        _uiState.update { it.copy(pushNotificationEnabled = enabled) }

    fun setSoundEnabled(enabled: Boolean) =
        _uiState.update { it.copy(soundEnabled = enabled) }

    fun setVibrationEnabled(enabled: Boolean) =
        _uiState.update { it.copy(vibrationEnabled = enabled) }

    fun setCourseRegistrationEnabled(enabled: Boolean) =
        _uiState.update { it.copy(courseRegistrationEnabled = enabled) }

    fun setAssignmentDeadlineEnabled(enabled: Boolean) =
        _uiState.update { it.copy(assignmentDeadlineEnabled = enabled) }

    fun setGradeReleaseEnabled(enabled: Boolean) =
        _uiState.update { it.copy(gradeReleaseEnabled = enabled) }

    fun setChapelEnabled(enabled: Boolean) =
        _uiState.update { it.copy(chapelEnabled = enabled) }

    fun setMarketingEnabled(enabled: Boolean) =
        _uiState.update { it.copy(marketingEnabled = enabled) }
}
