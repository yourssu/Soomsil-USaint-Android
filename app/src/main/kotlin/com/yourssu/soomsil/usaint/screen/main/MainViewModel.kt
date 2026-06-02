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
    val currentSemesterOnLeave: Boolean = false,
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
        // 현재 학기는 DB의 최신 행이 아니라 날짜 기준 유스케이스로 판별
        // (현재 학기가 아직 성적 미등록이라 DB에 없을 수 있음)
        val currentKey = getCurrentSemesterUseCase()
        buildUiState(student, summary, semesters, semesterWithLectures, chapelCard, currentKey)
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
        currentKey: Pair<Int, SemesterType>?,
    ): MainUiState {
        val ascending = semesters.sortedWith(compareBy({ it.year }, { it.semester.ordinal }))

        // 성적 추이는 전 학기를 종합한 추이이므로 휴학(미수강) 학기는 제외하고,
        // P/F 전용 학기(평점 0이지만 이수 학점 존재)는 직전 성적을 유지한다.
        val enrolled = ascending.filter { it.isEnrolled() }
        val barLastIndex = enrolled.lastIndex
        var carriedGpa = 0f
        val barData = enrolled.mapIndexed { index, semester ->
            val gpaValue = if (semester.gradePointsAverage > 0f) {
                carriedGpa = semester.gradePointsAverage
                semester.gradePointsAverage
            } else {
                carriedGpa
            }
            val isCurrent = index == barLastIndex
            GpaBarData(
                label = "${semester.year % 100}-${semester.semester.kor}",
                height = barHeight(gpaValue),
                isCurrent = isCurrent,
                gpaText = if (isCurrent) formatGpa(gpaValue) else null,
            )
        }

        val attendances = chapelCard?.chapelAttendances.orEmpty()
        val chapelTotal = attendances.size
        val chapelAttended = attendances.count { it.attendance == "출석" }
        val chapelProgress = if (chapelTotal > 0) chapelAttended.toFloat() / chapelTotal else 0f

        // 이번 학기는 날짜 기준 현재 학기(currentKey). DB에 해당 학기가 없거나 currentKey가
        // null이면(방학 등) 최신 학기로 폴백한다. 성적이 아직 없으면 성적 부분만 ?로 표시한다.
        val fallback = ascending.lastOrNull()
        val currentYear = currentKey?.first ?: fallback?.year
        val currentType = currentKey?.second ?: fallback?.semester
        val currentSemesterData = semesters.firstOrNull {
            it.year == currentYear && it.semester == currentType
        }
        val currentLectures = if (currentYear != null && currentType != null) {
            semesterWithLectures.entries
                .firstOrNull { it.key.year == currentYear && it.key.semester == currentType }
                ?.value.orEmpty()
        } else emptyList()
        val registered = currentSemesterData != null && currentLectures.isNotEmpty()
        val isOnLeave = student.status.contains("휴학")
        val currentTerm = if (currentYear != null && currentType != null) {
            termLabel(currentYear, currentType)
        } else ""

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
            currentSemesterTerm = currentTerm,
            currentSemesterRegistered = registered,
            currentSemesterGpa = if (registered) formatGpa(currentSemesterData!!.gradePointsAverage) else MainUiState.UNKNOWN,
            currentSemesterCredits = if (registered) formatCredit(currentSemesterData!!.earnedCredit) else MainUiState.UNKNOWN,
            currentSemesterCourseCount = if (registered) currentLectures.size.toString() else MainUiState.UNKNOWN,
            currentSemesterCourses = currentLectures.map { lecture ->
                SemesterCourseItem(
                    name = lecture.title,
                    professor = lecture.professor,
                    credit = formatCredit(lecture.credit),
                    grade = lecture.lectureGrade.toString(),
                )
            },
            currentSemesterOnLeave = isOnLeave && !registered,
        )
    }

    // 휴학/미수강 학기: 시도·취득 학점이 모두 0
    private fun SemesterData.isEnrolled(): Boolean =
        attemptedCredit > 0f || earnedCredit > 0f

    private fun termLabel(year: Int, type: SemesterType): String {
        val suffix = when (type) {
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
