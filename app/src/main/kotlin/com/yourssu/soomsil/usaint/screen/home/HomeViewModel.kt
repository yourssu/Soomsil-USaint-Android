package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.data.analytics.PostHogTracker
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.data.repository.UserDataRepository
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Home(
        val studentData: StudentData,
        val reportCardSummaryData: ReportCardSummaryData,
        val chapelCardData: ChapelData?,
        val currentSemesterLectures: List<LectureData>?,
        val currentSemesterData: SemesterData?,
        val showPasswordIncorrectSnackbar: MutableState<Boolean>,
        val showFailedLoadToStudentDataSnackbar: MutableState<Boolean>,
        val isFailedFetch: MutableState<Boolean>,
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val studentDataRepository: StudentDataRepository,
    private val reportCardRepository: ReportCardRepository,
    private val userDataRepository: UserDataRepository,
    private val chapelRepository: ChapelRepository,
    private val getCurrentSemesterUseCase: GetCurrentSemesterUseCase,
    private val posthogTracker: PostHogTracker,
) : ViewModel() {
    private var showPasswordIncorrectSnackbar = mutableStateOf(false)
    private var showFailedLoadToStudentDataSnackbar = mutableStateOf(false)
    //TODO Rusaint 수정될 시 삭제 바람
    private var isFailedFetch = mutableStateOf(false)
    val homeUiState: StateFlow<HomeUiState> =
        combine(
            studentDataRepository.studentData,
            reportCardRepository.reportCardSummaryData,
            chapelRepository.chapelCard,
            reportCardRepository.semesterWithLectures.map { semesterWithLecture ->
                val currentSemester = getCurrentSemesterUseCase()
                semesterWithLecture[semesterWithLecture.keys.find {
                    it.year == currentSemester?.first && it.semester == currentSemester.second
                }]
            },
            reportCardRepository.semesterWithLectures.map { semesterWithLecture ->
                val currentSemester = getCurrentSemesterUseCase()
                semesterWithLecture.keys.find {
                    it.year == currentSemester?.first && it.semester == currentSemester.second
                }
            },
            flowOf(showPasswordIncorrectSnackbar),
            flowOf(showFailedLoadToStudentDataSnackbar),
            flowOf(isFailedFetch),
            transform = HomeUiState::Home,
        )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeUiState.Loading,
            )

    // 사용자가 직접 pull to refresh를 했을 경우에만 true
    var isRefreshing by mutableStateOf(false)
        private set

    var hasInitialized by mutableStateOf(false)
        private set

    init {
        posthogTracker.trackHomeViewed()
        viewModelScope.launch {
            val autoFetch = userDataRepository.userData.first().autoFetch
            if (autoFetch) {
                fetchData(refresh = false)
            } else hasInitialized = true
        }
    }

    fun fetchData(refresh: Boolean) {
        if (isRefreshing || (!hasInitialized && refresh)) return
        viewModelScope.launch {
            isRefreshing = refresh
            try {
                studentDataRepository.fetchStudentData().onFailure { e ->
                    Timber.e(e)
                    if(e is RusaintException) throw e // 비밀번호가 중간단계부터 틀릴일은 없으니 맨 위에서 한번만 검사합니다
                }
                reportCardRepository.fetchCurrentSemesterLectures().onFailure { e -> Timber.e(e) }
                reportCardRepository.fetchReportCardSummary().onFailure { e -> Timber.e(e) }
                getCurrentSemesterUseCase()?.let {
                    chapelRepository.fetchChapelCardData(it)
                        .onFailure { e -> Timber.e(e) }
                }
            } catch(e: RusaintException) { // RusaintException이 아니면 중지할 필요 없음
                if(e.message?.contains("비밀번호") == true) {
                    // TODO 이 지점에서 비밀번호 틀려서 로그인에 실패한걸 트래커에 보낼 필요가 있을까?
                    //posthogTracker.trackLoginFailed(e)
                    showPasswordIncorrectSnackbar.value = true
2                }
                isFailedFetch.value = true
                // TODO 비밀번호가 문제가 아니라면 채플 정보만 재시도. Rusaint API 수정될 시 삭제바람
                getCurrentSemesterUseCase()?.let {
                    chapelRepository.fetchChapelCardData(it)
                        .onFailure { e -> Timber.e(e) }
                }
            }
            isRefreshing = false
            hasInitialized = true
        }
    }

    fun changePassword(password: String) {
        viewModelScope.launch {
            studentCredential.setPassword(password)
            fetchData(refresh = true)
        }
    }

    fun onCheckCurrentSemesterClicked() {
        viewModelScope.launch {
            posthogTracker.trackCurrentSemesterClick()
        }
    }

    fun onCheckReportCardClicked() {
        viewModelScope.launch {
            posthogTracker.trackNavigate("HOME", false)
        }
    }

    fun onCheckChapelCardClicked() {
        viewModelScope.launch {
            posthogTracker.trackNavigate("HOME", true)
        }
    }

    fun onCheckLectureItemClicked(lectureTitle: String) {
        viewModelScope.launch {
            posthogTracker.trackLectureDetail(lectureTitle)
        }
    }

    // 나중에 다른 위치로 옮겨도 좋을거같습니다
    private inline fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        flow7: Flow<T7>,
        flow8: Flow<T8>,
        crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R
    ): Flow<R> {
        return kotlinx.coroutines.flow.combine(flow, flow2, flow3, flow4, flow5, flow6, flow7, flow8) { args: Array<*> ->
            @Suppress("UNCHECKED_CAST")
            transform(
                args[0] as T1,
                args[1] as T2,
                args[2] as T3,
                args[3] as T4,
                args[4] as T5,
                args[5] as T6,
                args[6] as T7,
                args[7] as T8,
            )
        }
    }

}