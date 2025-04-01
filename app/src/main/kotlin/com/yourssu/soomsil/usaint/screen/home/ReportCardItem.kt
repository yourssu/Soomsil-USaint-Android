package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.ui.entities.Grade
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun ReportCardItem(
    reportCardSummary: ReportCardSummary,
    modifier: Modifier = Modifier,
    onReportCardClick: () -> Unit = {},
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        ) {
            Text(
                text = stringResource(id = R.string.saint_grade),
                modifier = Modifier
                    .padding(
                        top = 20.dp,
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            ActionTitle(
                title = stringResource(id = R.string.saint_grade_title),
                subTitle = stringResource(id = R.string.saint_grade_subtitle),
                onClick = onReportCardClick,
            )
            ReportCardSummary(
                reportCardSummary = reportCardSummary,
                onReportCardClick = onReportCardClick
            )
        }
    }
}

@Composable
private fun ReportCardSummary(
    reportCardSummary: ReportCardSummary,
    modifier: Modifier = Modifier,
    onReportCardClick: () -> Unit = {},
) {
    Column(modifier = modifier) {
        ReportOutline(
            title = stringResource(R.string.saint_grade_detail_average_grade),
            actualValue = reportCardSummary.gpa.formatToString(),
            maxValue = Grade.Max.formatToString(),
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 14.dp),
        )
        ReportOutline(
            title = stringResource(R.string.saint_grade_detail_creadit),
            actualValue = reportCardSummary.earnedCredit.formatToString(),
            maxValue = reportCardSummary.graduateCredit.formatToString(),
        )

        // 전체성적 보기 버튼
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(
//                    start = 16.dp,
//                    end = 16.dp,
//                    top = 10.dp,
//                )
//                .clip(RoundedCornerShape(8.dp))
//                .height(40.dp)
//                .background(color = MaterialTheme.colorScheme.surface)
//                .clickable(onClick = onReportCardClick),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Text(
//                text = stringResource(R.string.saint_grade_see_all)
//            )
//        }
//        BoxButton(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(
//                    start = 16.dp,
//                    end = 16.dp,
//                    top = 10.dp,
//                ),
//            onClick = onReportCardClick,
//            text = stringResource(R.string.saint_grade_see_all),
//            sizeType = BoxButtonSize.Medium,
//            buttonType = BoxButtonType.Filled,
//        )
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
        ReportCardItem(
            reportCardSummary = ReportCardSummary(
                gpa = 4.22.toGrade(),
                earnedCredit = 97.toCredit(),
                graduateCredit = 133.toCredit(),
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun ReportCardSummaryPreview() {
    SoomsilUSaintTheme {
        Surface {
            ReportCardSummary(
                reportCardSummary = ReportCardSummary(
                    gpa = 4.22.toGrade(),
                    earnedCredit = 97.toCredit(),
                    graduateCredit = 133.toCredit(),
                )
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
