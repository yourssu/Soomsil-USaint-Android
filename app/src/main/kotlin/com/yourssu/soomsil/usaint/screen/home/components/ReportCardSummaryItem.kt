package com.yourssu.soomsil.usaint.screen.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import com.yourssu.soomsil.usaint.ui.types.Grade

@Composable
fun ReportCardSummaryItem(
    studentData: StudentData?,
    reportCardSummary: ReportCardSummaryData?,
    modifier: Modifier = Modifier,
    onReportCardClick: () -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onReportCardClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        ) {
            Text(
                text = "내 성적",
                modifier = Modifier
                    .padding(
                        top = 20.dp,
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            ActionTitle(
                title = "성적 확인하기",
                subTitle = "지난학기",
            )
            ReportCardSummary(
                graduationPoints = studentData?.graduationPoints ?: 0f,
                reportCardSummary = reportCardSummary,
                onReportCardClick = onReportCardClick
            )
        }
    }
}

@Composable
private fun ReportCardSummary(
    graduationPoints: Float,
    reportCardSummary: ReportCardSummaryData?,
    modifier: Modifier = Modifier,
    onReportCardClick: () -> Unit = {},
) {
    Column(modifier = modifier) {
        ReportOutline(
            title = "평균학점",
            actualValue = reportCardSummary?.gradePointsAverage.toString(),
            maxValue = Grade.Max.formatToString(),
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 14.dp),
        )
        ReportOutline(
            title = "취득학점",
            actualValue = reportCardSummary?.earnedCredits.toString(),
            maxValue = graduationPoints.toString(),
        )
    }
}

@Composable
private fun ReportOutline(
    title: String,
    actualValue: String,
    maxValue: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(
                horizontal = 28.dp,
                vertical = 8.dp,
            ),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = actualValue,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "/",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
        Text(
            text = maxValue,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@PreviewLightDark
@Composable
private fun ReportCardItemPreview() {
    SoomsilUSaintTheme {
        ReportCardSummaryItem(
            studentData = StudentData.previewData,
            reportCardSummary = ReportCardSummaryData.previewData,
        )
    }
}

@PreviewLightDark
@Composable
private fun ReportCardSummaryPreview() {
    SoomsilUSaintTheme {
        Surface {
            ReportCardSummary(
                graduationPoints = 133f,
                reportCardSummary = ReportCardSummaryData.previewData,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ReportOutlinePreview() {
    SoomsilUSaintTheme {
        Surface {
            ReportOutline(
                title = "평균학점",
                actualValue = "12",
                maxValue = "123",
            )
        }
    }
}
