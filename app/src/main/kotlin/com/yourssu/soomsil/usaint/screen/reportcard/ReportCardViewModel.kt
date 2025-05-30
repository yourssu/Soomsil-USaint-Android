package com.yourssu.soomsil.usaint.screen.reportcard

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.StudentCredentialRepository
import com.yourssu.soomsil.usaint.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface ReportCardUiState {
    data object Loading : ReportCardUiState

    data class ReportCard(
        val summary: ReportCardSummaryData,
        val semesterWithLectures: Map<SemesterData, List<LectureData>>,
        val showPasswordIncorrectSnackbar: MutableState<Boolean>,
    ) : ReportCardUiState
}

sealed interface ReportCardUiEvent {
    data object FetchStart : ReportCardUiEvent
    data object FetchSuccess : ReportCardUiEvent
    data class FetchFailed(val message: String?) : ReportCardUiEvent
}

@HiltViewModel
class ReportCardViewModel @Inject constructor(
    private val studentCredential: StudentCredentialRepository,
    private val reportCardRepository: ReportCardRepository,
    userDataRepository: UserDataRepository,
) : ViewModel() {

    val showPasswordIncorrectSnackbar = mutableStateOf(false)

    val reportCardUiState: StateFlow<ReportCardUiState> = combine(
        reportCardRepository.reportCardSummaryData,
        reportCardRepository.semesterWithLectures,
    ) { summary, semesterWithLectures ->
        if (semesterWithLectures.isEmpty()) {
            ReportCardUiState.Loading
        } else {
            ReportCardUiState.ReportCard(summary, semesterWithLectures, showPasswordIncorrectSnackbar)
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReportCardUiState.Loading,
        )

    private val _eventChannel = Channel<ReportCardUiEvent>(Channel.BUFFERED)
    val reportCardEventFlow = _eventChannel.receiveAsFlow()

    var isFetching by mutableStateOf(false)
        private set

    // 사용자가 직접 pull to refresh를 했을 경우에만 true
    var isRefreshing by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            val autoFetch = userDataRepository.userData.first().autoFetch
            if (autoFetch || reportCardRepository.semesterWithLectures.first().isEmpty()) {
                try {
                    fetchData(refresh = false)
                } catch(e: RusaintException) {
                    e.localizedMessage
                }
            }
        }
    }

    fun fetchData(refresh: Boolean) {
        if (isFetching || isRefreshing) return
        viewModelScope.launch {
            if (!refresh) {
                _eventChannel.send(ReportCardUiEvent.FetchStart)
            }
            isFetching = true
            isRefreshing = refresh
            try {
                reportCardRepository.fetchSemesterWithLectures()
                    .onSuccess {
                        if (!refresh) {
                            _eventChannel.send(ReportCardUiEvent.FetchSuccess)
                        }
                    }
                    .onFailure { e ->
                        // TODO 에러처리
                        Timber.e(e)
                        _eventChannel.send(ReportCardUiEvent.FetchFailed(e.message))
                        if(e is RusaintException)
                            throw e
                    }
            } catch(e: RusaintException) { // RusaintException이 아니면 중지할 필요 없음
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