package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.BoxButton
import com.yourssu.design.system.compose.atom.BoxButtonSize
import com.yourssu.design.system.compose.atom.BoxButtonType
import com.yourssu.design.system.compose.atom.Divider
import com.yourssu.design.system.compose.atom.Thickness
import com.yourssu.design.system.compose.base.Surface
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.soomsil.usaint.R

@Composable
fun ReportCardItem(
    onReportCardClick: () -> Unit,
    modifier: Modifier = Modifier,
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
                color = YdsTheme.colors.textPrimary.copy(
                    alpha = 1f,
                ),
            )
            ActionTitle(
                title = stringResource(id = R.string.saint_grade_title),
                subTitle = stringResource(id = R.string.saint_grade_subtitle),
                onClick = onReportCardClick,
            )
            ReportCardSummary(onReportCardClick)
        }
    }
}

@Composable
private fun ReportCardSummary(onGradeCardClick: () -> Unit) {
    Column {
        GradeInfo(
            title = stringResource(id = R.string.saint_grade_detail_average_grade),
            actualValue = stringResource(id = R.string.average_grade_format, 4.06),
            maxValue = stringResource(id = R.string.average_grade_format, 4.50),
        )
        Divider(
            thickness = Thickness.Thin,
            modifier = Modifier.padding(horizontal = 14.dp),
        )
        GradeInfo(
            title = stringResource(id = R.string.saint_grade_detail_creadit),
            actualValue = "76.5",
            maxValue = "133",
        )
        Divider(
            thickness = Thickness.Thin,
            modifier = Modifier.padding(horizontal = 14.dp),
        )
        GradeInfo(
            title = stringResource(id = R.string.saint_grade_detail_overall_rank),
            actualValue = "25",
            maxValue = "70",
        )
        BoxButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 10.dp,
                ),
            onClick = onGradeCardClick,
            text = stringResource(id = R.string.saint_grade_see_all),
            leftIcon = com.yourssu.design.R.drawable.ic_board_line,
            sizeType = BoxButtonSize.Medium,
            buttonType = BoxButtonType.Line,
        )
    }
}

@Composable
private fun GradeInfo(
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

