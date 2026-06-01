package com.yourssu.soomsil.usaint.screen.main

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import com.yourssu.soomsil.usaint.screen.main.model.GpaBarData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = true,
    val greetingName: String = "",
    val notificationCount: Int = 0,
    val profileName: String = "",
    val department: String = "",
    val year: String = "",
    val status: String = "",
    val studentId: String = "",
    val gpa: String = "-",
    val maxGpa: String = MAX_GPA,
    val barData: List<GpaBarData> = emptyList(),
    val chapelAttended: Int = 0,
    val chapelTotal: Int = 0,
    val chapelProgress: Float = 0f,
) {
    companion object {
        const val MAX_GPA = "4.5"
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val studentDataRepository: StudentDataRepository,
    private val reportCardRepository: ReportCardRepository,
    private val chapelRepository: ChapelRepository,
    private val getCurrentSemesterUseCase: GetCurrentSemesterUseCase,
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = combine(
        studentDataRepository.studentData,
        reportCardRepository.reportCardSummaryData,
        reportCardRepository.semesterWithLectures,
        chapelRepository.chapelCard,
    ) { student, summary, semesterWithLectures, chapelCard ->
        buildUiState(student, summary, semesterWithLectures.keys, chapelCard)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState(isLoading = true),
    )

    init {
        viewModelScope.launch {
            if (reportCardRepository.semesterWithLectures.first().isEmpty()) {
                fetchData()
            }
        }
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                studentDataRepository.fetchStudentData().onFailure { e ->
                    Timber.e(e)
                    if (e is RusaintException) throw e
                }
                reportCardRepository.fetchSemesterWithLectures().onFailure { e ->
                    Timber.e(e)
                    if (e is RusaintException) throw e
                }
                getCurrentSemesterUseCase()?.let { semester ->
                    chapelRepository.fetchChapelCardData(semester).onFailure { e -> Timber.e(e) }
                }
            } catch (e: RusaintException) {
                Timber.e(e)
            }
        }
    }

    private fun buildUiState(
        student: StudentData,
        summary: ReportCardSummaryData,
        semesters: Set<SemesterData>,
        chapelCard: ChapelData?,
    ): MainUiState {
        val ascending = semesters.sortedWith(compareBy({ it.year }, { it.semester.ordinal }))
        val lastIndex = ascending.lastIndex
        val barData = ascending.mapIndexed { index, semester ->
            val isCurrent = index == lastIndex
            GpaBarData(
                label = "${semester.year % 100}-${semester.semester.kor}",
                height = barHeight(semester.gradePointsAverage),
                isCurrent = isCurrent,
                gpaText = if (isCurrent) formatGpa(semester.gradePointsAverage) else null,
            )
        }

        val attendances = chapelCard?.chapelAttendances.orEmpty()
        val chapelTotal = attendances.size
        val chapelAttended = attendances.count { it.attendance == "출석" }
        val chapelProgress = if (chapelTotal > 0) chapelAttended.toFloat() / chapelTotal else 0f

        return MainUiState(
            isLoading = student.name.isBlank(),
            greetingName = student.name,
            notificationCount = 0,
            profileName = student.name,
            department = student.majors.firstOrNull() ?: student.department,
            year = "${student.grade}학년",
            status = student.status,
            studentId = student.id,
            gpa = formatGpa(summary.gradePointsAverage),
            maxGpa = MainUiState.MAX_GPA,
            barData = barData,
            chapelAttended = chapelAttended,
            chapelTotal = chapelTotal,
            chapelProgress = chapelProgress,
        )
    }

    private fun barHeight(gpa: Float): Dp {
        val ratio = (gpa / MAX_GPA_VALUE).coerceIn(0f, 1f)
        return MIN_BAR_HEIGHT + (MAX_BAR_HEIGHT - MIN_BAR_HEIGHT) * ratio
    }

    private fun formatGpa(gpa: Float): String = "%.2f".format(Locale.US, gpa)

    companion object {
        private const val MAX_GPA_VALUE = 4.5f
        private val MIN_BAR_HEIGHT = 24.dp
        private val MAX_BAR_HEIGHT = 80.dp
    }
}
