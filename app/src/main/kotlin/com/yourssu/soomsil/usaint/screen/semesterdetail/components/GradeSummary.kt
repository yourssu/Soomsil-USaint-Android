package com.yourssu.soomsil.usaint.screen.semesterdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.domain.type.makeSemesterType
import com.yourssu.soomsil.usaint.screen.semesterdetail.CaptureFlag
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import com.yourssu.soomsil.usaint.ui.types.Credit
import com.yourssu.soomsil.usaint.ui.types.Grade
import com.yourssu.soomsil.usaint.ui.types.Semester
import com.yourssu.soomsil.usaint.ui.types.toCredit
import com.yourssu.soomsil.usaint.ui.types.toGrade
import java.text.DecimalFormat

@Composable
fun GradeSummary(
    semesterName: String,
    gpa: Grade,
    earnedCredit: Credit,   // 취득 학점
    semesterRank: Int,      // 학기별 석차
    semesterMaxRank: Int,
    overallRank: Int,       // 전체 석차
    overallMaxRank: Int,
    captureFlag: CaptureFlag,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp,
            )
    ) {
        Column {
            if (captureFlag !is CaptureFlag.None) {
                // 캡처 화면에서 필요함
                Text(
                    text = semesterName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(2.dp))
            }
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = gpa.formatToString(),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "/",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = Grade.Max.formatToString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        SummaryList(
            title = "취득학점",
            actualValue = earnedCredit.value,
            modifier = Modifier.fillMaxWidth(),
        )
        SummaryList(
            title = "학기별 석차",
            actualValue = semesterRank.toFloat(),
            maxValue = semesterMaxRank,
            modifier = Modifier.fillMaxWidth(),
        )
        SummaryList(
            title = "전체 석차",
            actualValue = overallRank.toFloat(),
            maxValue = overallMaxRank,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SummaryList(
    title: String,
    actualValue: Float,
    modifier: Modifier = Modifier,
    maxValue: Int? = null,
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = if (actualValue > 0) {
                DecimalFormat("0.##").format(actualValue) // 하위 소수점 0 버림
            } else {
                "-"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        maxValue?.let {
            Spacer(Modifier.width(2.dp))
            Text(
                text = "/$it",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun GradeSummaryPreview() {
    val semester = Semester(
        makeSemesterType(2020, "1"),
        gpa = 4.06.toGrade(),
        earnedCredit = 17.5.toCredit(),
        semesterRank = 15,
        semesterStudentCount = 55,
        overallRank = 12,
        overallStudentCount = 100,
    )
    SoomsilUSaintTheme {
        GradeSummary(
            semesterName = semester.type.fullName,
            gpa = semester.gpa,
            earnedCredit = semester.earnedCredit,
            semesterRank = semester.semesterRank,
            semesterMaxRank = semester.semesterStudentCount,
            overallRank = semester.overallRank,
            overallMaxRank = semester.overallStudentCount,
            captureFlag = CaptureFlag.Original,
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        )
    }
}
