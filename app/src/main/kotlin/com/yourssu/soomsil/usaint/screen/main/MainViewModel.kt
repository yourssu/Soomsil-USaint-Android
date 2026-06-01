package com.yourssu.soomsil.usaint.screen.main

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.repository.ChapelRepository
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.StudentDataRepository
import com.yourssu.soomsil.usaint.domain.usecase.GetCurrentSemesterUseCase
import com.yourssu.soomsil.usaint.screen.main.model.GpaBarData
import com.yourssu.soomsil.usaint.screen.main.model.SemesterCourseItem
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
    // 이번 학기 성적 바텀시트용
    val currentSemesterTerm: String = "",
    val currentSemesterRegistered: Boolean = false,
    val currentSemesterGpa: String = UNKNOWN,
    val currentSemesterCredits: String = UNKNOWN,
    val currentSemesterCourseCount: String = UNKNOWN,
    val currentSemesterCourses: List<SemesterCourseItem> = emptyList(),
) {
    companion object {
        const val MAX_GPA = "4.5"
        const val UNKNOWN = "?"
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
        reportCardRepository.semesters,
        reportCardRepository.semesterWithLectures,
        chapelRepository.chapelCard,
    ) { student, summary, semesters, semesterWithLectures, chapelCard ->
        buildUiState(student, summary, semesters, semesterWithLectures, chapelCard)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState(isLoading = true),
    )

    init {
        viewModelScope.launch {
            if (reportCardRepository.semesters.first().isEmpty()) {
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
        semesters: List<SemesterData>,
        semesterWithLectures: Map<SemesterData, List<LectureData>>,
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

        // 이번 학기(가장 최근 학기) 성적. 강의가 적재되지 않았으면 '미등록'으로 보고 ?를 표시한다.
        val currentSemester = ascending.lastOrNull()
        val currentLectures = currentSemester?.let { sel ->
            semesterWithLectures.entries
                .firstOrNull { it.key.year == sel.year && it.key.semester == sel.semester }
                ?.value
        }.orEmpty()
        val registered = currentLectures.isNotEmpty()

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
            currentSemesterTerm = currentSemester?.termLabel() ?: "",
            currentSemesterRegistered = registered,
            currentSemesterGpa = if (registered) formatGpa(currentSemester!!.gradePointsAverage) else MainUiState.UNKNOWN,
            currentSemesterCredits = if (registered) formatCredit(currentSemester!!.earnedCredit) else MainUiState.UNKNOWN,
            currentSemesterCourseCount = if (registered) currentLectures.size.toString() else MainUiState.UNKNOWN,
            currentSemesterCourses = currentLectures.map { lecture ->
                SemesterCourseItem(
                    name = lecture.title,
                    professor = lecture.professor,
                    credit = formatCredit(lecture.credit),
                    grade = lecture.lectureGrade.toString(),
                )
            },
        )
    }

    private fun SemesterData.termLabel(): String {
        val suffix = when (semester) {
            SemesterType.One -> "1학기"
            SemesterType.Two -> "2학기"
            SemesterType.Summer -> "여름학기"
            SemesterType.Winter -> "겨울학기"
        }
        return "${year}년 $suffix"
    }

    private fun formatCredit(credit: Float): String =
        if (credit % 1f == 0f) credit.toInt().toString() else "%.1f".format(Locale.US, credit)

    // GPA에 0 기준으로 비례하는 높이(막대 바닥을 공유하므로 높이만으로 비교 가능).
    // GPA가 0이어도 최소한의 막대는 보이도록 floor를 둔다.
    private fun barHeight(gpa: Float): Dp {
        val ratio = (gpa / MAX_GPA_VALUE).coerceIn(0f, 1f)
        return (MAX_BAR_HEIGHT * ratio).coerceAtLeast(MIN_BAR_HEIGHT)
    }

    private fun formatGpa(gpa: Float): String = "%.2f".format(Locale.US, gpa)

    companion object {
        private const val MAX_GPA_VALUE = 4.5f
        private val MIN_BAR_HEIGHT = 6.dp
        private val MAX_BAR_HEIGHT = 90.dp
    }
}
