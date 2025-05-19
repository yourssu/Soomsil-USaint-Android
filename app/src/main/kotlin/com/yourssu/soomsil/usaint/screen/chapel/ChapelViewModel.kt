package com.yourssu.soomsil.usaint.screen.chapel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface ChapelUiState {
    data object Loading : ChapelUiState

    data class ChapelCard(
        val chapelCard: ChapelData?,
        val chapels: List<ChapelData>,
    ): ChapelUiState
}

@HiltViewModel
class ChapelViewModel @Inject constructor(
    private val chapelRepository: ChapelRepository,
    private val getCurrentSemesterUseCase: GetCurrentSemesterUseCase,
) : ViewModel() {
    val chapelCardUiState: StateFlow<ChapelUiState> = combine(
        chapelRepository.chapelCard,
        chapelRepository.chapels,
        transform = ChapelUiState::ChapelCard,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChapelUiState.Loading,

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

            getCurrentSemesterUseCase.invoke()?.let {
                chapelRepository.fetchChapelCardData(it)
                    .onFailure { e -> Timber.e(e) }
            }

            chapelRepository.fetchSemesterWithChapels().onFailure { e -> Timber.e(e) }
            isFetching = false
            isRefreshing = false
        }
    }

}