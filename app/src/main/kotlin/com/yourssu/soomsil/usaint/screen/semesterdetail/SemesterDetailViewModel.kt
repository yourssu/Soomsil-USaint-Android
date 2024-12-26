package com.yourssu.soomsil.usaint.screen.semesterdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.CurrentSemesterRepository
import com.yourssu.soomsil.usaint.data.repository.LectureRepository
import com.yourssu.soomsil.usaint.data.repository.SemesterRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.domain.type.SemesterType
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.LectureInfo
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.sortByGrade
import com.yourssu.soomsil.usaint.ui.entities.toLectureInfo
import com.yourssu.soomsil.usaint.ui.entities.toSemester
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.USaintSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SemesterDetailViewModel @Inject constructor(
    private val uSaintSessionRepo: USaintSessionRepository,
    private val semesterRepo: SemesterRepository,
    private val lectureRepo: LectureRepository,
    private val currentSemesterRepo: CurrentSemesterRepository
) : ViewModel() {
    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var isRefreshing by mutableStateOf(false)
        private set

    var semesters: List<Semester> by mutableStateOf(emptyList())
        private set
    val semesterLecturesMap: MutableMap<SemesterType, List<LectureInfo>> = mutableStateMapOf()

    private var currentSemester: SemesterType = currentSemesterRepo.getCurrentSemesterType()

    init {
        initialize()
    }

    fun refresh(semester: SemesterType) {
        viewModelScope.launch {
            isRefreshing = true
            refreshLectureInfos(semester)
            isRefreshing = false
        }
    }

    fun initialRefresh(semester: SemesterType) {
        // 현재 학기인 경우 항상 갱신
        if (semester == currentSemester || semesterLecturesMap[semester].isNullOrEmpty()) {
            viewModelScope.launch {
                refreshLectureInfos(semester)
            }
        }
    }

    private fun initialize() {
        viewModelScope.launch {
            semesterRepo.getAllLocalSemesters()
                .onSuccess { semesterVOs ->
                    semesters = semesterVOs.map {
                        val semester = it.toSemester()
                        semesterLecturesMap[semester.type] = emptyList()
                        semester
                    }.sortedBy { it.type }
                }
                .onFailure { e -> Timber.e(e) }

            semesters.forEach { semester ->
                lectureRepo.getLocalLectures(semester.type)
                    .onSuccess { lectureVOs ->
                        semesterLecturesMap[semester.type] = lectureVOs
                            .map { it.toLectureInfo() }
                            .sortByGrade()
                    }
                    .onFailure { e -> Timber.e(e) }
            }
        }
    }

    private var session: USaintSession? = null
    private var mutex = Mutex()

    private suspend fun refreshLectureInfos(semester: SemesterType) {
        // 동시에 로그인 여러 번 하지 않도록
        mutex.withLock {
            if (session == null) {
                session = uSaintSessionRepo.getSession().getOrElse { e ->
                    Timber.e(e)
                    _uiEvent.emit(UiEvent.SessionFailure)
                    return
                }
            }
        }
        lectureRepo.getRemoteLectures(session!!, semester)
            .onSuccess { lectureVOs ->
                // update ui state
                semesterLecturesMap[semester] = lectureVOs
                    .map { it.toLectureInfo() }
                    .sortByGrade()
                // store
                lectureRepo.storeLectures(*lectureVOs.toTypedArray())

                // 현재 학기는 학기 정보도 갱신시켜줘야 함
                if (semester == currentSemester) {
                    Timber.d("update semester")
                    val newSemesterVO =
                        currentSemesterRepo.updateLocalCurrentSemester(lectureVOs).getOrElse { e ->
                            Timber.e(e)
                            _uiEvent.emit(UiEvent.Failure("학기 정보를 갱신하는 도중 문제가 발생했습니다."))
                            return@onSuccess
                        }
                    semesters = semesters.dropLast(1) + listOf(newSemesterVO.toSemester())
                }
            }
            .onFailure { e ->
                Timber.e(e)
                _uiEvent.emit(UiEvent.RefreshFailure)
            }
        session = null
    }
}