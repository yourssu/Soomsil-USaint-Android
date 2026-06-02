package com.yourssu.soomsil.usaint.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.StudentCredentialRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyPageUiState(
    val gradeNotificationEnabled: Boolean = true,
    val campusNotificationEnabled: Boolean = true,
)

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val studentCredentialRepository: StudentCredentialRepository,
    private val studentDataRepository: StudentDataRepository,
    private val reportCardRepository: ReportCardRepository,
    private val chapelRepository: ChapelRepository,
) : ViewModel() {
    // UserData에는 단일 notificationEnabled 필드만 있어 성적 알림에 매핑(영속화)하고,
    // 캠퍼스 알림은 대응 필드가 없어 VM 내부 상태로 관리한다.
    private val campusNotificationEnabled = MutableStateFlow(true)

    val uiState: StateFlow<MyPageUiState> = combine(
        userDataRepository.userData,
        campusNotificationEnabled,
    ) { userData, campus ->
        MyPageUiState(
            gradeNotificationEnabled = userData.notificationEnabled,
            campusNotificationEnabled = campus,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MyPageUiState(),
    )

    fun setGradeNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNotificationEnabled(enabled)
        }
    }

    fun setCampusNotificationEnabled(enabled: Boolean) {
        campusNotificationEnabled.value = enabled
    }

    fun logout() {
        viewModelScope.launch {
            reportCardRepository.deleteAll()
            studentCredentialRepository.clear()
            studentDataRepository.clear()
            userDataRepository.clear()
            chapelRepository.deleteAll()
        }
    }
}
