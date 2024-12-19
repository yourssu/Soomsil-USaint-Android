package com.yourssu.soomsil.usaint.screen.semesterlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SemesterListViewModel @Inject constructor(
    private val reportCardSummaryRepo: ReportCardRepository,
) : ViewModel() {
    private val _uiState: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiState = _uiState.asSharedFlow()

    var isRefreshing by mutableStateOf(false)
        private set
    var includeSeasonalSemester by mutableStateOf(false)

    var reportCardSummary: ReportCardSummary by mutableStateOf(ReportCardSummary())
        private set
    var semesters: List<Semester> by mutableStateOf(emptyList())
        private set

    init {
        initReportCardSummary()
    }

    fun refresh() {

    }

    private fun initReportCardSummary() {
        viewModelScope.launch {
            reportCardSummaryRepo.getLocalReportCard().onSuccess { reportCard ->
                reportCardSummary = ReportCardSummary(
                    gpa = reportCard.gpa.toGrade(),
                    earnedCredit = reportCard.earnedCredit.toCredit(),
                    graduateCredit = reportCard.graduateCredit.toCredit(),
                )
            }.onFailure { e -> Timber.e(e) }
        }
    }

    private fun initSemesters() {
        // 비어있는 경우 refresh
    }
}