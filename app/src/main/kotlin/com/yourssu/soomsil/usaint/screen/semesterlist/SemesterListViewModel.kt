package com.yourssu.soomsil.usaint.screen.semesterlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.CurrentSemesterRepository
import com.yourssu.soomsil.usaint.data.repository.SemesterRepository
import com.yourssu.soomsil.usaint.data.repository.TotalReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.domain.type.SemesterType
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterTypeUseCase
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.toReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.toSemester
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SemesterListViewModel @Inject constructor(
    private val uSaintSessionRepo: USaintSessionRepository,
    private val totalReportCardRepo: TotalReportCardRepository,
    private val semesterRepo: SemesterRepository,
    private val currentSemesterRepo: CurrentSemesterRepository,
    getCurrentSemesterTypeUseCase: GetCurrentSemesterTypeUseCase,
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

    val currentSemester: SemesterType? = getCurrentSemesterTypeUseCase()

    // job 정의
    private var refreshJob: Job? = null

    init {
        initialize()
    }

    fun refresh() {
        // 이전에 진행 중이던 refreshJob이 있으면 취소
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            isRefreshing = true
            refreshSemesters()
            isRefreshing = false
        }
    }

    fun cancelJob() {
        Timber.d("SemesterListViewModel cancelJob")
        isRefreshing = false
        refreshJob?.cancel()
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
                        refreshSemesters()
                    } else {
                        semesters = semesterList
                            .map { vo -> vo.toSemester() }
                            .sortedBy { it.type }
                    }
                }
                .onFailure { e -> Timber.e(e) }
        }
    }

    private var session: USaintSession? = null
    private var mutex = Mutex()

    private suspend fun refreshSemesters() {
        // 동시에 로그인 여러 번 하지 않도록
        mutex.withLock {
            if (session == null) {
                session = uSaintSessionRepo.getSession().getOrElse { e ->
                    Timber.e(e)
                    _uiEvent.emit(UiEvent.SessionFailure)
                    return
                }
            }
        }
        val semesterVOTemp = ArrayList<SemesterVO>()
        val totalReportCardDeferred = viewModelScope.async {
            totalReportCardRepo.getRemoteReportCard(session!!)
        }
        val semesterDeferred = viewModelScope.async {
            semesterRepo.getAllRemoteSemesters(session!!)
        }

        // ui state 변경 및 DB 갱신
        val totalReportCard = totalReportCardDeferred.await().getOrElse { e ->
            Timber.e(e)
            when (e) {
                is RusaintException -> _uiEvent.emit(UiEvent.RefreshFailure)
                else -> _uiEvent.emit(UiEvent.Failure())
            }
            session = null
            return
        }

        semesterDeferred.await()
            .onSuccess { semesterVOs ->
                semesterVOTemp.addAll(semesterVOs)
            }
            .onFailure { e ->
                Timber.e(e)
                when (e) {
                    is RusaintException -> _uiEvent.emit(UiEvent.RefreshFailure)
                    else -> _uiEvent.emit(UiEvent.Failure())
                }
                session = null
                return
            }

        // semesterDeferred와 겹치면 오류나기 때문에 따로 실행
        currentSemesterRepo.getRemoteCurrentSemester(session!!)
            .onSuccess { currentSemesterVO ->
                if (currentSemesterVO == null) return@onSuccess
                semesterVOTemp.add(currentSemesterVO)
            }
            .onFailure { e ->
                Timber.e(e)
                when (e) {
                    is RusaintException -> _uiEvent.emit(UiEvent.RefreshFailure)
                    else -> _uiEvent.emit(UiEvent.Failure())
                }
                session = null
                return
            }

        reportCardSummary = totalReportCard.toReportCardSummary()
        semesters = semesterVOTemp
            .map { it.toSemester() }
            .sortedBy { it.type }

        totalReportCardRepo.storeReportCard(totalReportCard)
        semesterRepo.storeSemesters(*semesterVOTemp.toTypedArray())

        _uiEvent.emit(UiEvent.Success)
        session = null
    }
}