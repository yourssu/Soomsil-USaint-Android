package com.yourssu.soomsil.usaint.screen.reportcard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.analytics.PostHogTracker
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
    private val posthogTracker: PostHogTracker,
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
            ReportCardUiState.ReportCard(
                summary,
                semesterWithLectures,
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReportCardUiState.Loading,
        )

    private val _eventChannel = Channel<ReportCardUiEvent>(Channel.BUFFERED)
    val reportCardEventFlow = _eventChannel.receiveAsFlow()

    // 사용자가 직접 pull to refresh를 했을 경우에만 true
    var isRefreshing by mutableStateOf(false)
        private set

    var hasInitialized by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            val autoFetch = userDataRepository.userData.first().autoFetch
            if (autoFetch || reportCardRepository.semesterWithLectures.first().isEmpty()) {
                fetchData(refresh = false)
            } else hasInitialized = true
        }
    }

    fun fetchData(refresh: Boolean) {
        if (isRefreshing || (!hasInitialized && refresh)) return
        viewModelScope.launch {
            if (!refresh) {
                _eventChannel.send(ReportCardUiEvent.FetchStart)
            }
            isRefreshing = refresh

            reportCardRepository.fetchSemesterWithLectures()
                .onSuccess {
                    if (!refresh) {
                        _eventChannel.send(ReportCardUiEvent.FetchSuccess)
                    }
                }
                .onFailure { e ->
                    Timber.e(e)
                    if (e is RusaintException) {
                        if (e.message?.contains("비밀번호") == true) {
                            // TODO 이 지점에서 비밀번호 틀려서 로그인에 실패한걸 트래커에 보낼 필요가 있을까?
                            //posthogTracker.trackLoginFailed(e)
                            showPasswordIncorrectSnackbar.value = true
                            return@onFailure
                        }
                    }
                    _eventChannel.send(ReportCardUiEvent.FetchFailed(e.message))

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

    fun onCheckLectureItemClicked(lectureTitle: String) {
        viewModelScope.launch {
            posthogTracker.trackLectureDetail(lectureTitle)
        }
    }

    fun onCheckSemesterItemClicked(semester: SemesterData) {
        viewModelScope.launch {
            posthogTracker.trackSemester(semester)
        }
    }
}
