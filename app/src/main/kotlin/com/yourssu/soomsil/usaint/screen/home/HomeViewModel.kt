package com.yourssu.soomsil.usaint.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Home(
        val studentData: StudentData,
        val reportCardSummaryData: ReportCardSummaryData,
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    studentDataRepository: StudentDataRepository,
    reportCardRepository: ReportCardRepository,
) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> =
        combine(
            studentDataRepository.studentData,
            reportCardRepository.reportCardSummaryData,
            transform = HomeUiState::Home,
        )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeUiState.Loading,
            )
}