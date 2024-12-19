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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.Divider
import com.yourssu.design.system.compose.atom.Thickness
import com.yourssu.design.system.compose.base.Surface
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.ui.entities.Grade
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade

@Composable
fun ReportCardItem(
    reportCardSummary: ReportCardSummary,
    modifier: Modifier = Modifier,
    onReportCardClick: () -> Unit = {},
) {
    Surface(
        rounding = 8.dp,
        color = YdsTheme.colors.bgNormal,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        ) {
            YdsText(
                text = stringResource(id = R.string.saint_grade),
                modifier = Modifier
                    .padding(
                        top = 20.dp,
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                style = YdsTheme.typography.title3,
                color = YdsTheme.colors.textPrimary,
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
        Divider(
            thickness = Thickness.Thin,
            modifier = Modifier.padding(horizontal = 14.dp),
        )
        ReportOutline(
            title = stringResource(R.string.saint_grade_detail_creadit),
            actualValue = reportCardSummary.earnedCredit.formatToString(),
            maxValue = reportCardSummary.graduateCredit.formatToString(),
        )

        // 전체성적 보기 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 10.dp,
                )
                .clip(RoundedCornerShape(8.dp))
                .height(40.dp)
                .background(color = YdsTheme.colors.bgSelected)
                .clickable(onClick = onReportCardClick),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            YdsText(
                text = stringResource(R.string.saint_grade_see_all)
            )
        }
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
        YdsText(
            text = title,
            modifier = Modifier.weight(1f),
            style = YdsTheme.typography.body1,
            color = YdsTheme.colors.textSecondary,
        )
        YdsText(
            text = actualValue,
            style = YdsTheme.typography.subTitle2.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = YdsTheme.colors.textPointed,
        )
        YdsText(
            text = "/",
            style = YdsTheme.typography.caption0.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = YdsTheme.colors.textTertiary,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
        YdsText(
            text = maxValue,
            style = YdsTheme.typography.caption0.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = YdsTheme.colors.textTertiary,
        )
    }
}

@PreviewLightDark
@Composable
private fun ReportCardItemPreview() {
    YdsTheme {
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
    YdsTheme {
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
    YdsTheme {
        Surface {
            ReportOutline(
                title = "평균학점",
                actualValue = "12",
                maxValue = "123",
            )
        }
    }
}
