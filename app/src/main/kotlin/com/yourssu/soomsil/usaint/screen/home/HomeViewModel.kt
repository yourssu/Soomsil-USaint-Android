package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.ChapelAttendanceData
import com.yourssu.soomsil.usaint.core.model.ChapelSimpleData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.data.repository.UserDataRepository
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Home(
        val studentData: StudentData,
        val reportCardSummaryData: ReportCardSummaryData,
        val chapelCardData: ChapelSimpleData,
        val chapelCardAttendancesData: List<ChapelAttendanceData>
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val studentDataRepository: StudentDataRepository,
    private val reportCardRepository: ReportCardRepository,
    private val userDataRepository: UserDataRepository,
    private val chapelRepository: ChapelRepository,
    private val getCurrentSemesterUseCase: GetCurrentSemesterUseCase,
) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> =
        combine(
            studentDataRepository.studentData,
            reportCardRepository.reportCardSummaryData,
            chapelRepository.chapelCardData,
            chapelRepository.chapelCardAttendanceData,
            transform = HomeUiState::Home,
        )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeUiState.Loading,
            )

    var isFetching by mutableStateOf(false)
        private set

    // 사용자가 직접 pull to refresh를 했을 경우에만 true
    var isRefreshing by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            val autoFetch = userDataRepository.userData.first().autoFetch
            if (autoFetch) {
                fetchData(refresh = false)
            }
        }
    }

    fun fetchData(refresh: Boolean) {
        if (isFetching || isRefreshing) return
        viewModelScope.launch {
            isFetching = true
            isRefreshing = refresh
            studentDataRepository.fetchStudentData().onFailure { e -> Timber.e(e) }
            reportCardRepository.fetchReportCardSummary().onFailure { e -> Timber.e(e) }
            getCurrentSemesterUseCase.invoke()?.let {
                chapelRepository.fetchChapelCardData(it)
                    .onFailure { e -> Timber.e(e) }
            }
            isFetching = false
            isRefreshing = false
        }
    }
}