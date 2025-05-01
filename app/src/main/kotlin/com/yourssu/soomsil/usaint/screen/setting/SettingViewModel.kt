package com.yourssu.soomsil.usaint.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.StudentCredentialRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val reportCardRepository: ReportCardRepository,
    private val studentCredentialRepository: StudentCredentialRepository,
    private val studentDataRepository: StudentDataRepository,
    private val userDataRepository: UserDataRepository,
//    private val updateWorkerUseCase: UpdateWorkerUseCase,
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

    fun updateNotificationSetting(enable: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNotificationEnabled(enable)
//            if (enable) {
//                updateWorkerUseCase.enqueue()
//            } else {
//                updateWorkerUseCase.dequeue()
//            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            reportCardRepository.deleteAll()
            studentCredentialRepository.clear()
            studentDataRepository.clear()
            userDataRepository.clear()

            // TODO dequeue workmanager
        }
    }
}

sealed interface SettingUiState {
    data object Loading : SettingUiState

    data class UserEditableSettings(
        val notificationEnabled: Boolean,
    ) : SettingUiState
}
