package com.yourssu.soomsil.usaint.screen.reportcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.util.SortedMap
import javax.inject.Inject

sealed interface ReportCardUiState {
    data object Loading : ReportCardUiState

    data class ReportCard(
        val summary: ReportCardSummaryData,
        val semesters: SortedMap<SemesterData, List<LectureData>>,
    ) : ReportCardUiState
}

class ReportCardViewModel @Inject constructor(
    private val reportCardRepository: ReportCardRepository,
) : ViewModel() {
    val reportCardUiState: StateFlow<ReportCardUiState> = combine(
        reportCardRepository.reportCardSummaryData,
        reportCardRepository.semesterWithLectures,
        transform = ReportCardUiState::ReportCard,
    )
        .onStart { /* TODO init */ }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReportCardUiState.Loading,
        )

}