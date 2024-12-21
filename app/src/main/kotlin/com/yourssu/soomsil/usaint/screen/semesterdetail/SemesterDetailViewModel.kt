package com.yourssu.soomsil.usaint.screen.semesterdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.LectureRepository
import com.yourssu.soomsil.usaint.data.repository.SemesterRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.data.type.SemesterType
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.LectureInfo
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.toLectureInfo
import com.yourssu.soomsil.usaint.ui.entities.toSemester
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SemesterDetailViewModel @Inject constructor(
    private val uSaintSessionRepo: USaintSessionRepository,
    private val semesterRepo: SemesterRepository,
    private val lectureRepo: LectureRepository,
) : ViewModel() {
    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var isRefreshing by mutableStateOf(false)
        private set

    var semesters: List<Semester> by mutableStateOf(emptyList())
        private set
    val semesterLecturesMap: MutableMap<SemesterType, List<LectureInfo>> = mutableStateMapOf()

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

    private fun initialize() {
        viewModelScope.launch {
            semesterRepo.getAllLocalSemesters()
                .onSuccess { semesterVOs ->
                    semesters = semesterVOs.map {
                        val semester = it.toSemester()
                        semesterLecturesMap[semester.type] = emptyList()
                        semester
                    }
                }
                .onFailure { e -> Timber.e(e) }

            semesters.forEach { semester ->
                lectureRepo.getLocalLectures(semester.type)
                    .onSuccess { lectureVOs ->
                        semesterLecturesMap[semester.type] = lectureVOs.map { it.toLectureInfo() }
                    }
                    .onFailure { e -> Timber.e(e) }
            }
        }
    }

    private suspend fun refreshLectureInfos(semester: SemesterType) {
        val session = uSaintSessionRepo.getSession().getOrElse { e ->
            Timber.e(e)
            _uiEvent.emit(UiEvent.SessionFailure)
            return
        }
        lectureRepo.getRemoteLectures(session, semester)
            .onSuccess { lectureVOs ->
                // update ui state
                semesterLecturesMap[semester] = lectureVOs.map { it.toLectureInfo() }
                // store
                lectureRepo.storeLectures(*lectureVOs.toTypedArray())
            }
            .onFailure { e -> Timber.e(e) }
    }
}