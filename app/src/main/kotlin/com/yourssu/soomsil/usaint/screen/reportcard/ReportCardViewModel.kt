package com.yourssu.soomsil.usaint.screen.reportcard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface ReportCardUiState {
    data object Loading : ReportCardUiState

    data class ReportCard(
        val summary: ReportCardSummaryData,
        val semesterWithLectures: Map<SemesterData, List<LectureData>>,
    ) : ReportCardUiState
}

@HiltViewModel
class ReportCardViewModel @Inject constructor(
    private val reportCardRepository: ReportCardRepository,
) : ViewModel() {
    val reportCardUiState: StateFlow<ReportCardUiState> = combine(
        reportCardRepository.reportCardSummaryData,
        reportCardRepository.semesterWithLectures,
        transform = ReportCardUiState::ReportCard,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReportCardUiState.Loading,
        )

    var isFetching by mutableStateOf(false)
        private set

    // 사용자가 직접 pull to refresh를 했을 경우에만 true
    var isRefreshing by mutableStateOf(false)
        private set

    init {
        fetchData(refresh = false)
    }

    fun fetchData(refresh: Boolean) {
        if (isFetching || isRefreshing) return
        viewModelScope.launch {
            isFetching = true
            isRefreshing = refresh
            // TODO 에러처리
            reportCardRepository.fetchSemesterWithLectures().onFailure { e -> Timber.e(e) }
            isFetching = false
            isRefreshing = false
        }
    }
}