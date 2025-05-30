package com.yourssu.soomsil.usaint.screen.chapel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface ChapelUiState {
    data object Loading : ChapelUiState

    data class Chapel(
        val chapelCard: ChapelData?,
        val chapels: List<ChapelData>,
        val showPasswordIncorrectSnackbar: MutableState<Boolean>,
    ): ChapelUiState
}

@HiltViewModel
class ChapelViewModel @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val chapelRepository: ChapelRepository,
    private val getCurrentSemesterUseCase: GetCurrentSemesterUseCase,
) : ViewModel() {
    private var showPasswordIncorrectSnackbar = mutableStateOf(false)

    val chapelUiState: StateFlow<ChapelUiState> = combine(
        chapelRepository.chapelCard,
        chapelRepository.chapels,
        flowOf(showPasswordIncorrectSnackbar),
        transform = ChapelUiState::Chapel,
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
            try {
                getCurrentSemesterUseCase()?.let {
                    chapelRepository.fetchChapelCardData(it)
                        .onFailure { e ->
                            Timber.e(e)
                            if(e is RusaintException) throw e
                        }
                }
                chapelRepository.fetchSemesterWithChapels().onFailure { e -> Timber.e(e) }
            } catch(e: RusaintException) {
                if(e.message?.contains("비밀번호") == true) {
                    showPasswordIncorrectSnackbar.value = true
                }
            }

            isFetching = false
            isRefreshing = false
        }
    }

    fun changePassword(password: String) {
        viewModelScope.launch {
            studentCredential.setPassword(password)
            fetchData(refresh = true)
        }
    }

}