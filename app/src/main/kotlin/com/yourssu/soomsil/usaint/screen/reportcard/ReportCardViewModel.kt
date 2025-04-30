package com.yourssu.soomsil.usaint.screen.reportcard

import androidx.lifecycle.ViewModel
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import javax.inject.Inject

sealed interface ReportCardUiState {
    data object Loading : ReportCardUiState

    data class ReportCard(
        val summary: ReportCardSummaryData,
        val semesterWithLectures: Map<SemesterData, List<LectureData>>,
    ) : ReportCardUiState
}

class ReportCardViewModel @Inject constructor(
//    private val reportCardRepository: ReportCardRepository,
) : ViewModel() {
//    val reportCardUiState: StateFlow<ReportCardUiState> = combine(
//        reportCardRepository.reportCardSummaryData,
//        reportCardRepository.semesterWithLectures,
//        transform = ReportCardUiState::ReportCard,
//    )
//        .onStart { /* TODO init */ }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = ReportCardUiState.Loading,
//        )

}