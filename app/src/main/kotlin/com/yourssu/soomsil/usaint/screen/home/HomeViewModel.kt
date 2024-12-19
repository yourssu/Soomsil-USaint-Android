package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.StudentInfoRepository
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.StudentInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val studentInfoRepo: StudentInfoRepository,
) : ViewModel() {
    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var studentInfo: StudentInfo? by mutableStateOf(null)
        private set

    init {
        getStudentInfo()
    }

    fun refresh() {
        viewModelScope.launch {
            studentInfoRepo.getStudentInfo().onSuccess { stu ->
                studentInfo = StudentInfo(
                    name = stu.name,
                    department = stu.department,
                    grade = stu.grade.toInt(),
                )
            }.onFailure { e -> Timber.e(e) }
        }
    }

    private fun getStudentInfo() {
        viewModelScope.launch {
            studentInfoRepo.getStudentInfoFromDataStore().onSuccess { stu ->
                studentInfo = StudentInfo(
                    name = stu.name,
                    department = stu.department,
                    grade = stu.grade.toInt(),
                )
            }.onFailure { e -> Timber.e(e) }
        }
    }
}