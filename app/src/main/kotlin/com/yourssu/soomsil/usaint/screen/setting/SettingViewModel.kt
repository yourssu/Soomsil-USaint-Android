package com.yourssu.soomsil.usaint.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.StudentInfoRepository
import com.yourssu.soomsil.usaint.screen.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val studentInfoRepository: StudentInfoRepository
): ViewModel() {

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            studentInfoRepository.deleteStudentInfo()
            _uiEvent.emit(UiEvent.Success)
        }
    }
}