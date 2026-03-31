package com.yourssu.soomsil.usaint.screen.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.core.model.ReportCardSummaryData
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun ReportCardItem(
    reportCardSummary: ReportCardSummaryData?,
    modifier: Modifier = Modifier,
    onReportCardClick: () -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onReportCardClick,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            ReportOutline(
                title = "평균학점",
                actualValue = reportCardSummary?.gradePointsAverage.toString(),
                maxValue = 4.5f.toString(),
            )
            ReportOutline(
                title = "취득학점",
                actualValue = reportCardSummary?.earnedCredits.toString(),
                maxValue = reportCardSummary?.graduationPoints.toString(),
            )
        }
    }
}

@Composable
fun ReportCardItemEmpty(
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "아직 등록된 성적이 없어요",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

// TODO 따로 분리하기
@Composable
fun ReportOutline(
    title: String,
    actualValue: String,
    maxValue: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = actualValue,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "/",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            Text(
                text = maxValue,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ReportCardItemPreview() {
    SoomsilUSaintTheme {
        ReportCardItem(
            reportCardSummary = ReportCardSummaryData.previewData,
        )
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
