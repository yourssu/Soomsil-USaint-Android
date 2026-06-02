package com.yourssu.soomsil.usaint.screen.chapel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
    studentDataRepository: StudentDataRepository,
) : ViewModel() {
    private var showPasswordIncorrectSnackbar = mutableStateOf(false)

    // 휴학 중인 학생은 수강할 채플이 없음 (학적 상태로 판별)
    val isOnLeave: StateFlow<Boolean> = studentDataRepository.studentData
        .map { it.status.contains("휴학") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

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

    var hasInitialized by mutableStateOf(false)
        private set

    // 사용자가 직접 pull to refresh를 했을 경우에만 true
    var isRefreshing by mutableStateOf(false)
        private set


    init {
        fetchData(refresh = false)
    }

    fun fetchData(refresh: Boolean) {
        if (isRefreshing || (!hasInitialized && refresh)) return
        viewModelScope.launch {
            isRefreshing = refresh
            try {
                getCurrentSemesterUseCase()?.let {
                    chapelRepository.fetchChapelCardData(it)
                        .onFailure { e ->
                            Timber.e(e)
                            if(e is RusaintException) throw e
                        }
                }
            } catch(e: RusaintException) {
                if(e.message?.contains("비밀번호") == true) {
                    showPasswordIncorrectSnackbar.value = true
                }
            } finally {
                // RusaintException catch에서 무조건 비밀번호 관련이 아닐 수 있습니다
                // 현재 학기가 존재하지 않는데 채플 정보를 불러오려고 하면 그때도 RusaintException이 발생하는거 같습니다
                // finally 구문에서 비밀번호 관련 에러로 catch된게 아니면 불러오도록 하겠습니다
                if(!showPasswordIncorrectSnackbar.value)
                    chapelRepository.fetchSemesterWithChapels().onFailure { e -> Timber.e(e) }
            }
            hasInitialized = true
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