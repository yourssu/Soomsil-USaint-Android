package com.yourssu.soomsil.usaint.screen.grade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.repository.ReportCardRepository
import com.yourssu.soomsil.usaint.screen.grade.model.CourseItem
import com.yourssu.soomsil.usaint.screen.grade.model.GpaPoint
import com.yourssu.soomsil.usaint.screen.grade.model.SemesterTab
import com.yourssu.soomsil.usaint.screen.grade.model.gradeStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

data class GradeUiState(
    val isLoading: Boolean = true,
    val semesters: List<SemesterTab> = emptyList(),
    val gpaPoints: List<GpaPoint> = emptyList(),
    val courses: List<CourseItem> = emptyList(),
    val gpa: String = "-",
    val maxGpa: String = MAX_GPA,
    val credits: String = "-",
    val courseCount: String = "0",
    val rank: String = "-",
) {
    companion object {
        const val MAX_GPA = "4.5"
    }
}

@HiltViewModel
class GradeViewModel @Inject constructor(
    private val reportCardRepository: ReportCardRepository,
) : ViewModel() {
    // 사용자가 선택한 학기 탭 인덱스(최신 학기 우선 정렬 기준). 기본값은 가장 최근 학기.
    private val selectedIndex = MutableStateFlow(0)

    val uiState: StateFlow<GradeUiState> = combine(
        reportCardRepository.semesterWithLectures,
        selectedIndex,
    ) { semesterWithLectures, selected ->
        if (semesterWithLectures.isEmpty()) {
            GradeUiState(isLoading = true)
        } else {
            buildUiState(semesterWithLectures, selected)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GradeUiState(isLoading = true),
    )

    init {
        viewModelScope.launch {
            if (reportCardRepository.semesterWithLectures.first().isEmpty()) {
                fetchData()
            }
        }
    }

    fun onTabClick(index: Int) {
        selectedIndex.value = index
    }

    fun fetchData() {
        viewModelScope.launch {
            reportCardRepository.fetchSemesterWithLectures()
                .onFailure { e ->
                    Timber.e(e)
                    if (e is RusaintException) throw e
                }
        }
    }

    private fun buildUiState(
        semesterWithLectures: Map<SemesterData, List<LectureData>>,
        selected: Int,
    ): GradeUiState {
        // 오래된 학기 → 최신 학기 순 (GPA 추이 차트용)
        val ascending = semesterWithLectures.keys.sortedWith(
            compareBy({ it.year }, { it.semester.ordinal })
        )
        // 최신 학기 → 오래된 학기 순 (탭 표시용)
        val descending = ascending.reversed()
        val safeIndex = selected.coerceIn(0, (descending.size - 1).coerceAtLeast(0))
        val selectedSemester = descending.getOrNull(safeIndex)

        val semesterTabs = descending.mapIndexed { index, semester ->
            SemesterTab(label = semester.tabLabel(), isActive = index == safeIndex)
        }

        val gpaPoints = ascending.map { semester ->
            GpaPoint(
                semester = semester.chartLabel(),
                gpa = semester.gradePointsAverage,
                isCurrent = semester == selectedSemester,
            )
        }

        val lectures = selectedSemester?.let { semesterWithLectures[it] }.orEmpty()
        val courses = lectures.map { lecture ->
            val grade = lecture.lectureGrade.toString()
            val (dot, gradeColor, badgeBg) = gradeStyle(grade)
            CourseItem(
                name = lecture.title,
                professor = lecture.professor,
                credit = "${formatCredit(lecture.credit)}학점",
                grade = grade,
                dotColor = dot,
                gradeColor = gradeColor,
                badgeBgColor = badgeBg,
            )
        }

        return GradeUiState(
            isLoading = false,
            semesters = semesterTabs,
            gpaPoints = gpaPoints,
            courses = courses,
            gpa = selectedSemester?.let { "%.2f".format(Locale.US, it.gradePointsAverage) } ?: "-",
            maxGpa = GradeUiState.MAX_GPA,
            credits = selectedSemester?.let { formatCredit(it.earnedCredit) } ?: "-",
            courseCount = courses.size.toString(),
            rank = selectedSemester?.let { "${it.semesterRank.first}위" } ?: "-",
        )
    }

    private fun formatCredit(credit: Float): String =
        if (credit % 1f == 0f) credit.toInt().toString() else "%.1f".format(Locale.US, credit)

    private fun SemesterData.tabLabel(): String = "${year}년 ${semester.semesterLabel()}"

    private fun SemesterData.chartLabel(): String = "${year % 100}-${semester.kor}"

    private fun SemesterType.semesterLabel(): String = when (this) {
        SemesterType.One -> "1학기"
        SemesterType.Two -> "2학기"
        SemesterType.Summer -> "여름학기"
        SemesterType.Winter -> "겨울학기"
    }
}
