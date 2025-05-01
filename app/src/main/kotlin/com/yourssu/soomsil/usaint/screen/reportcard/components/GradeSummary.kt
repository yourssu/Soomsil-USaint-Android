package com.yourssu.soomsil.usaint.screen.reportcard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.domain.type.makeSemesterType
import com.yourssu.soomsil.usaint.screen.semesterdetail.CaptureFlag
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import com.yourssu.soomsil.usaint.ui.types.Semester
import com.yourssu.soomsil.usaint.ui.types.toCredit
import com.yourssu.soomsil.usaint.ui.types.toGrade
import java.text.DecimalFormat

@Composable
fun GradeSummary(
    gpa: Float,
    earnedCredit: Float,   // 취득 학점
    semesterRank: Pair<Int, Int>,      // 학기별 석차
    generalRank: Pair<Int, Int>,       // 전체 석차
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = String.format("%.2f", gpa),
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
                    text = "4.50",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            SummaryList(
                title = "취득학점",
                actualValue = earnedCredit,
                modifier = Modifier.fillMaxWidth(),
            )
            SummaryList(
                title = "학기별 석차",
                actualValue = semesterRank.first.toFloat(),
                maxValue = semesterRank.second,
                modifier = Modifier.fillMaxWidth(),
            )
            SummaryList(
                title = "전체 석차",
                actualValue = generalRank.first.toFloat(),
                maxValue = generalRank.second,
                modifier = Modifier.fillMaxWidth(),
            )
        }
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
    SoomsilUSaintTheme {
        GradeSummary(
            gpa = 4.13f,
            earnedCredit = 100f,
            semesterRank = 3 to 100,
            generalRank = 4 to 123,
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        )
    }
}
