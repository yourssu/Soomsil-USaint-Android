package com.yourssu.soomsil.usaint.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.analytics.PosthogTracker
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.StudentCredentialRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.data.repository.UserDataRepository
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingUiState {
    data object Loading : SettingUiState

    data class UserEditableSettings(
        val notificationEnabled: Boolean,
        val currentSemesterSpecified: Boolean,
        val specifiedCurrentSemester: Pair<Int, SemesterType>?,
        val autoFetchEnabled: Boolean,
    ) : SettingUiState
}

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val reportCardRepository: ReportCardRepository,
    private val studentCredentialRepository: StudentCredentialRepository,
    private val studentDataRepository: StudentDataRepository,
    private val userDataRepository: UserDataRepository,
    private val chapelRepository: ChapelRepository,
    private val getCurrentSemesterUseCase: GetCurrentSemesterUseCase,
    private val posthogTracker: PosthogTracker,
//    private val updateWorkerUseCase: UpdateWorkerUseCase,
) : ViewModel() {
    val uiState: StateFlow<SettingUiState> =
        userDataRepository.userData
            .map {
                SettingUiState.UserEditableSettings(
                    notificationEnabled = it.notificationEnabled,
                    currentSemesterSpecified = it.isCurrentSemesterSpecified,
                    specifiedCurrentSemester = if (it.isCurrentSemesterSpecified) {
                        it.specifiedCurrentSemester
                    } else {
                        getCurrentSemesterUseCase.default()
                    },
                    autoFetchEnabled = it.autoFetch,
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

    fun updateAutoFetchEnabled(enable: Boolean) {
        viewModelScope.launch {
            userDataRepository.setAutoFetch(enable)
            posthogTracker.trackAutoLoadClick()
        }
        //
    }

    fun logout() {
        viewModelScope.launch {
            posthogTracker.trackLogout()
            reportCardRepository.deleteAll()
            studentCredentialRepository.clear()
            studentDataRepository.clear()
            userDataRepository.clear()
            chapelRepository.deleteAll()
            // TODO dequeue workmanager
        }
    }

    fun specifyCurrentSemester(year: Int, semester: SemesterType) {
        viewModelScope.launch {
            userDataRepository.setCurrentSemesterSpecified(year, semester)
        }
    }

    fun changePassword(password: String) {
        viewModelScope.launch {
            studentCredentialRepository.setPassword(password)
        }
    }

    fun unspecifiedCurrentSemester() {
        viewModelScope.launch {
            userDataRepository.setCurrentSemesterUnspecified()
        }
    }
}
