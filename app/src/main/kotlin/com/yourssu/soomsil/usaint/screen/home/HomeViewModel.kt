package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.StudentInfoRepository
import com.yourssu.soomsil.usaint.data.repository.TotalReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.StudentInfo
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import com.yourssu.soomsil.usaint.ui.entities.toReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.toStudentInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val uSaintSessionRepo: USaintSessionRepository,
    private val studentInfoRepo: StudentInfoRepository,
    private val totalReportCardRepo: TotalReportCardRepository,
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
    private var initJob: Job? = null
    private var refreshJob: Job? = null

    init {
        initialize()
    }

    override fun onCleared() {
        super.onCleared()
        // ViewModel이 소멸될 때 Job도 함께 취소
        cancelJob()
    }

    fun cancelJob() {
        Timber.d("HomeViewModel cancelJob")
        isRefreshing = false
        initJob?.cancel()
        refreshJob?.cancel()
    }

    fun refresh() {
        // 이전에 진행 중이던 refreshJob이 있으면 취소
        refreshJob?.cancel()

        refreshJob = viewModelScope.launch {
            try {
                isRefreshing = true
                val session = uSaintSessionRepo.getSession().getOrElse { e ->
                    Timber.e(e)
                    _uiEvent.emit(UiEvent.SessionFailure)
                    isRefreshing = false
                    return@launch
                }
                val stuDto = studentInfoRepo.getRemoteStudentInfo(session).getOrElse { e ->
                    Timber.e(e)
                    when (e) {
                        is RusaintException -> _uiEvent.emit(UiEvent.RefreshFailure)
                        else -> _uiEvent.emit(UiEvent.Failure())
                    }
                    isRefreshing = false
                    return@launch
                }
                val totalReportCard =
                    totalReportCardRepo.getRemoteReportCard(session).getOrElse { e ->
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
                reportCardSummary = ReportCardSummary(
                    gpa = totalReportCard.gpa.toGrade(),
                    earnedCredit = totalReportCard.earnedCredit.toCredit(),
                    graduateCredit = totalReportCard.graduateCredit.toCredit(),
                )
                // DB 갱신
                totalReportCardRepo.storeReportCard(totalReportCard).onFailure { e -> Timber.e(e) }
                studentInfoRepo.storeStudentInfo(stuDto).onFailure { e -> Timber.e(e) }
                _uiEvent.emit(UiEvent.Success)
                isRefreshing = false
            } catch (e:CancellationException) {
                Timber.e("Job was cancelled: $e")
            }
        }
    }

    private fun initialize() {

        // 이전에 진행 중이던 initJob이 있으면 취소
        initJob?.cancel()

        initJob = viewModelScope.launch {
            try {
                studentInfoRepo.getLocalStudentInfo()
                    .onSuccess { stu ->
                        studentInfo = stu.toStudentInfo()
                    }
                    .onFailure { e -> Timber.e(e) }
                totalReportCardRepo.getLocalReportCard()
                    .onSuccess { totalReportCard ->
                        reportCardSummary = totalReportCard.toReportCardSummary()
                    }
                    .onFailure { e -> Timber.e(e) }
            } catch (e: CancellationException) {
                Timber.e("Job was cancelled: $e")
            }
        }
    }
}