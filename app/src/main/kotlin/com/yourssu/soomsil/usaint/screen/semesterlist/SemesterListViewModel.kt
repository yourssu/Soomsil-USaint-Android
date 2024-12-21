package com.yourssu.soomsil.usaint.screen.semesterlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.SemesterRepository
import com.yourssu.soomsil.usaint.data.repository.TotalReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.data.type.toSemesterType
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.toReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.toSemester
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.core.SemesterType
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
                    Timber.d(semesterList.toString()) // fixme: 가끔씩 빈 리스트 들어오는 경우 있음
                    if (semesterList.isEmpty()) {
                        // 비어있는 경우 refresh
                        isRefreshing = true
                        refreshAll()
                        isRefreshing = false
                    } else {
                        semesters = semesterList.map { vo -> vo.toSemester() }
                    }
                }
                .onFailure { e -> Timber.e(e) }
        }
    }

    private suspend fun refreshAll() {
        val session = uSaintSessionRepo.getSession().getOrElse { e ->
            Timber.e(e)
            _uiEvent.emit(UiEvent.SessionFailure)
            return
        }
        val totalReportCardDeferred = viewModelScope.async {
            totalReportCardRepo.getRemoteReportCard(session)
        }
        val semesterVOListDeferred = viewModelScope.async {
            semesterRepo.getAllRemoteSemesters(session)
        }

        // ui state 변경 및 DB 갱신
        totalReportCardDeferred.await()
            .onSuccess {
                reportCardSummary = it.toReportCardSummary()
                totalReportCardRepo.storeReportCard(it)
            }
            .onFailure { e ->
                Timber.e(e)
                when (e) {
                    is RusaintException -> _uiEvent.emit(UiEvent.RefreshFailure)
                    else -> _uiEvent.emit(UiEvent.Failure())
                }
                return
            }
        semesterVOListDeferred.await()
            .onSuccess {
                val reversed = it.reversed()
                semesters = reversed.map { vo -> vo.toSemester() }
                semesterRepo.storeSemesters(*reversed.toTypedArray())
            }
            .onFailure { e ->
                Timber.e(e)
                when (e) {
                    is RusaintException -> _uiEvent.emit(UiEvent.RefreshFailure)
                    else -> _uiEvent.emit(UiEvent.Failure())
                }
                return
            }

        Timber.d("getCurrentSemester")
        semesterRepo.getCurrentSemester(session)
            .onSuccess { semesterVO ->
                Timber.d("current semester: ${semesterVO?.year} ${semesterVO?.semester}")
                if (semesterVO != null) {
                    semesterRepo.storeSemesters(semesterVO)
                    semesters = semesters + listOf(semesterVO).map { vo -> vo.toSemester() }
                    Timber.d(semesters.toString())
                }

                semesterRepo.getLocalSemester(2024, SemesterType.TWO.toSemesterType(2024)).onSuccess {
                    Timber.d(it.toString())
                }.onFailure {
                    Timber.e(it)
                }
            }
            .onFailure { e -> Timber.e(e) }

        _uiEvent.emit(UiEvent.Success)
    }
}