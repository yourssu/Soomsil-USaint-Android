package com.yourssu.soomsil.usaint.screen.semesterlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.LectureRepository
import com.yourssu.soomsil.usaint.data.repository.SemesterRepository
import com.yourssu.soomsil.usaint.data.repository.TotalReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.domain.type.SemesterType
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterTypeUseCase
import com.yourssu.soomsil.usaint.domain.usecase.MakeSemesterFromLecturesUseCase
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.toReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.toSemester
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.joinAll
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
    private val lectureRepo: LectureRepository,
    private val makeSemesterUseCase: MakeSemesterFromLecturesUseCase,
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
                        refreshJob = launch { refreshSemesters() }
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

        val job1 = viewModelScope.launch {
            totalReportCardRepo.getRemoteReportCard(session!!)
                .onSuccess { totalReportCard ->
                    reportCardSummary = totalReportCard.toReportCardSummary()
                    totalReportCardRepo.storeReportCard(totalReportCard)
                }
                .onFailure { e -> handleError(e) }
        }

        val job2 = viewModelScope.launch {
            val semestersTemp = ArrayList<Semester>()
            val semesterVOs = semesterRepo.getAllRemoteSemesters(session!!).getOrElse { e ->
                handleError(e)
                return@launch
            }
            semestersTemp.addAll(semesterVOs.map { it.toSemester() })
            semesterRepo.storeSemesters(*semesterVOs.toTypedArray())

            // 최근 학기에 대한 상세 성적 정보 요청
            if (currentSemester != null && semestersTemp.find { it.type == currentSemester } == null) {
                val currentLectureVOs =
                    lectureRepo.getRemoteLectures(session!!, currentSemester).getOrElse { e ->
                        handleError(e, "최근 학기 정보를 가져오지 못했습니다.")
                        return@launch
                    }
                if (currentLectureVOs.isNotEmpty()) {
                    val currentSemester = makeSemesterUseCase(currentSemester, currentLectureVOs)
                    semesterRepo.storeSemesters(currentSemester)
                    lectureRepo.storeLectures(*currentLectureVOs.toTypedArray())
                    semestersTemp.add(currentSemester.toSemester())
                }
            }

            semesters = semestersTemp.sortedBy { it.type }
        }

        // 모두 완료될 때까지 기다림
        joinAll(job1, job2)
        session = null
    }

    private suspend fun handleError(e: Throwable, msg: String? = null) {
        Timber.e(e)
        when {
            e is RusaintException && msg == null -> _uiEvent.emit(UiEvent.RefreshFailure)
            else -> _uiEvent.emit(UiEvent.Failure(msg))
        }
    }
}