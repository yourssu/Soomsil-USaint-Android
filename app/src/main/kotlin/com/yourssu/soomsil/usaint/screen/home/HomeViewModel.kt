package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.types.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.types.StudentInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val uSaintSessionRepo: USaintSessionRepository,
    private val studentInfoRepo: StudentDataRepository,
) : ViewModel() {
    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var isRefreshing: Boolean by mutableStateOf(false)
        private set
    var studentInfo: StudentInfo? by mutableStateOf(null)
        private set
    var reportCardSummary: ReportCardSummary by mutableStateOf(ReportCardSummary())
        private set

    // job 정의
    private var refreshJob: Job? = null

    init {
        initialize()
    }

    fun cancelJob() {
        Timber.d("HomeViewModel cancelJob")
        isRefreshing = false
        refreshJob?.cancel()
    }

    fun refresh() {
        // 이전에 진행 중이던 refreshJob이 있으면 취소
        refreshJob?.cancel()

        refreshJob = viewModelScope.launch {
            isRefreshing = true
            val millis = measureTimeMillis { refreshHome() }
            Timber.d("Refresh Home: ${millis}ms") // about 6500ms
            isRefreshing = false
        }
    }

    private fun initialize() {
        viewModelScope.launch {
//            studentInfoRepo.getLocalStudentInfo()
//                .onSuccess { stu ->
//                    studentInfo = stu.toStudentInfo()
//                }
//                .onFailure { e -> Timber.e(e) }
        }
    }

    private suspend fun refreshHome() {
        val session = uSaintSessionRepo.getSession().getOrElse { e ->
            Timber.e(e)
            _uiEvent.emit(UiEvent.SessionFailure)
            return
        }

        val job1 = viewModelScope.launch {
//            studentInfoRepo.getRemoteStudentInfo(session)
//                .onSuccess { stuDto ->
//                    studentInfo = StudentInfo(
//                        name = stuDto.name,
//                        department = stuDto.department,
//                        grade = stuDto.grade.toInt(),
//                    )
//                    studentInfoRepo.storeStudentInfo(stuDto)
//                }
//                .getOrElse { e ->
//                    handleError(e)
//                    return@launch
//                }
        }


        joinAll(job1)
    }

    private suspend fun handleError(e: Throwable, msg: String? = null) {
        Timber.e(e)
        when {
            e is RusaintException && msg == null -> _uiEvent.emit(UiEvent.RefreshFailure)
            else -> _uiEvent.emit(UiEvent.Failure(msg))
        }
    }
}