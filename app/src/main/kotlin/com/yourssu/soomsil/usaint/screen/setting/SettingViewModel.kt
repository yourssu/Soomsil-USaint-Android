package com.yourssu.soomsil.usaint.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.yourssu.soomsil.usaint.data.repository.StudentInfoRepository
import com.yourssu.soomsil.usaint.screen.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingState(
    val showDialog: Boolean = false
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val studentInfoRepository: StudentInfoRepository
): ViewModel() {

    private val _state = MutableStateFlow(SettingState())
    val state: MutableStateFlow<SettingState> = _state

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updateDialogState(showDialog: Boolean) {
        _state.value = _state.value.copy(showDialog = showDialog)
    }
    fun logout() {
        viewModelScope.launch {
            studentInfoRepository.deleteStudentInfo()
            _uiEvent.emit(UiEvent.Success)
        }
    }
}