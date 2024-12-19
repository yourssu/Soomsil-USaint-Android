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
import dev.eatsteak.rusaint.ffi.RusaintException
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

    var isRefreshing: Boolean by mutableStateOf(false)
        private set
    var studentInfo: StudentInfo? by mutableStateOf(null)
        private set

    init {
        getStudentInfo()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing = true
            val stuDto = studentInfoRepo.getStudentInfo().getOrElse { e ->
                Timber.e(e)
                val errMsg = when (e) {
                    is RusaintException -> "새로고침에 실패했습니다. 다시 시도해주세요."
                    else -> "알 수 없는 문제가 발생했습니다."
                }
                _uiEvent.emit(UiEvent.Failure(errMsg))
                isRefreshing = false
                return@launch
            }
            // ui state 변경
            studentInfo = StudentInfo(
                name = stuDto.name,
                department = stuDto.department,
                grade = stuDto.grade.toInt(),
            )
            // DataStore에 저장
            studentInfoRepo.storeStudentInfo(stuDto).onFailure { e -> Timber.e(e) }
            _uiEvent.emit(UiEvent.Success)
            isRefreshing = false
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