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
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
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
        initReportCardSummary()
        initSemesters()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing = true
            val session = uSaintSessionRepo.getSession().getOrElse { e ->
                Timber.e(e)
                _uiEvent.emit(UiEvent.Failure("로그인 실패: 비밀번호를 확인해주세요."))
                isRefreshing = false
                return@launch
            }
            val totalReportCard = totalReportCardRepo.getRemoteReportCard(session).getOrElse { e ->
                Timber.e(e)
                val errMsg = when (e) {
                    is RusaintException -> "새로고침에 실패했습니다. 다시 시도해주세요."
                    else -> "알 수 없는 문제가 발생했습니다."
                }
                _uiEvent.emit(UiEvent.Failure(errMsg))
                isRefreshing = false
                return@launch
            }
            val semesterVOList = semesterRepo.getAllRemoteSemesters(session).getOrElse { e ->
                Timber.e(e)
                // TODO: 오류 문구 수정
                _uiEvent.emit(UiEvent.Failure("실패!"))
                isRefreshing = false
                return@launch
            }
            // ui state 변경
            reportCardSummary = ReportCardSummary(
                gpa = totalReportCard.gpa.toGrade(),
                earnedCredit = totalReportCard.earnedCredit.toCredit(),
                graduateCredit = totalReportCard.graduateCredit.toCredit(),
            )
            semesters = semesterVOList.map { vo ->
                Semester(
                    axisName = "1-1", // TODO
                    fullName = String.format("%d년 %s학기", vo.year, vo.semester),
                    gpa = vo.gpa.toGrade(),
                    earnedCredit = vo.earnedCredit.toCredit(),
                    isSeasonal = !(vo.semester.contains("1") || vo.semester.contains("2"))
                )
            }
            // TODO DB 갱신. HomeViewModel이랑 비슷하게 하면 됨

            _uiEvent.emit(UiEvent.Success)
            isRefreshing = false
        }
    }

    private fun initReportCardSummary() {
        viewModelScope.launch {
            totalReportCardRepo.getLocalReportCard().onSuccess { reportCard ->
                reportCardSummary = ReportCardSummary(
                    gpa = reportCard.gpa.toGrade(),
                    earnedCredit = reportCard.earnedCredit.toCredit(),
                    graduateCredit = reportCard.graduateCredit.toCredit(),
                )
            }.onFailure { e -> Timber.e(e) }
        }
    }

    private fun initSemesters() {
        // TODO: 로컬 DB에서 semester 정보 가져오기
        // 비어있는 경우 refresh
    }
}