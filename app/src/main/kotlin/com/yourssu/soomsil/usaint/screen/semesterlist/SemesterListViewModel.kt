package com.yourssu.soomsil.usaint.screen.semesterlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.SemesterRepository
import com.yourssu.soomsil.usaint.data.repository.TotalReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.toReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.toSemester
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SemesterListViewModel @Inject constructor(
    private val uSaintSessionRepo: USaintSessionRepository,
    private val totalReportCardRepo: TotalReportCardRepository,
    private val semesterRepo: SemesterRepository,
) : ViewModel() {
    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var isRefreshing by mutableStateOf(false)
        private set
    var includeSeasonalSemester by mutableStateOf(false)

    var reportCardSummary: ReportCardSummary by mutableStateOf(ReportCardSummary())
        private set
    var semesters: List<Semester> by mutableStateOf(emptyList())
        private set

    init {
        initialize()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing = true
            refreshAll()
            isRefreshing = false
        }
    }

    private fun initialize() {
        viewModelScope.launch {
            totalReportCardRepo.getLocalReportCard()
                .onSuccess { reportCard ->
                    reportCardSummary = reportCard.toReportCardSummary()
                }
                .onFailure { e -> Timber.e(e) }
            semesterRepo.getAllLocalSemesters()
                .onSuccess { semesterList ->
                    semesters = semesterList.map { vo -> vo.toSemester() }
                }
                .onFailure { e -> Timber.e(e) }
            if (semesters.isEmpty()) {
                // 비어있는 경우 refresh
                isRefreshing = true
                refreshAll()
                isRefreshing = false
            }
        }
    }

    private suspend fun refreshAll() {
        val session = uSaintSessionRepo.getSession().getOrElse { e ->
            Timber.e(e)
            _uiEvent.emit(UiEvent.Failure("로그인 실패: 비밀번호 또는 네트워크를 확인해주세요."))
            return
        }
        val totalReportCard = viewModelScope.async {
            totalReportCardRepo.getRemoteReportCard(session).getOrElse { e ->
                Timber.e(e)
                val errMsg = when (e) {
                    is RusaintException -> "새로고침에 실패했습니다. 다시 시도해주세요."
                    else -> "알 수 없는 문제가 발생했습니다."
                }
                _uiEvent.emit(UiEvent.Failure(errMsg))
                null
            }
        }
        val semesterVOList = viewModelScope.async {
            semesterRepo.getAllRemoteSemesters(session).getOrElse { e ->
                Timber.e(e)
                val errMsg = when (e) {
                    is RusaintException -> "새로고침에 실패했습니다. 다시 시도해주세요."
                    else -> "알 수 없는 문제가 발생했습니다."
                }
                _uiEvent.emit(UiEvent.Failure(errMsg))
                null
            }
        }
        // ui state 변경
        totalReportCard.await()?.let {
            reportCardSummary = it.toReportCardSummary()
        }
        semesterVOList.await()?.let {
            semesters = it.map { vo -> vo.toSemester() }
        }
        _uiEvent.emit(UiEvent.Success)
    }
}