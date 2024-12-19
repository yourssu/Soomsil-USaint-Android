package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.ProfileImageView
import com.yourssu.design.system.compose.base.Icon
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.design.system.compose.base.ydsClickable
import com.yourssu.design.system.compose.component.topbar.SingleTitleTopBar
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.ui.entities.Credit
import com.yourssu.soomsil.usaint.ui.entities.Grade
import com.yourssu.soomsil.usaint.ui.entities.StudentInfo
import com.yourssu.soomsil.usaint.ui.entities.TotalReportCardInfo
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import com.yourssu.design.R as YdsR

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onReportCardClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    HomeScreen(
        studentInfo = viewModel.studentInfo,
        totalReportCardInfo = TotalReportCardInfo(Grade.Zero, Credit.Zero, Credit.Zero), // TODO
        onProfileClick = onProfileClick,
        onSettingClick = onSettingClick,
        onReportCardClick = onReportCardClick,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    studentInfo: StudentInfo?,
    totalReportCardInfo: TotalReportCardInfo,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onReportCardClick: () -> Unit = {},
) {
    YdsScaffold(
        modifier = modifier,
        topBar = {
            SingleTitleTopBar(title = stringResource(id = R.string.saint_title))
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(YdsTheme.colors.bgSelected)
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp,
                ),
        ) {
            StudentInfoItem(
                studentInfo = studentInfo,
                onProfileClick = onProfileClick,
                onSettingClick = {
                    onSettingClick()
                },
            )
            Spacer(Modifier.height(12.dp))
            ReportCardItem(
                totalReportCardInfo = totalReportCardInfo,
                onReportCardClick = onReportCardClick,
            )
        }
    }
}

@Composable
fun ActionTitle(
    title: String,
    subTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp,
            )
            .ydsClickable(
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileImageView(painter = painterResource(id = R.drawable.default_profile_image))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            YdsText(
                text = subTitle,
                style = YdsTheme.typography.subTitle3,
                color = YdsTheme.colors.textSecondary,
            )
            YdsText(
                text = title,
                style = YdsTheme.typography.subTitle1,
                color = YdsTheme.colors.textPrimary,
            )
        }
        Icon(
            id = YdsR.drawable.ic_arrow_right_line,
            tint = YdsTheme.colors.textPrimary,
        )
    }
}


@PreviewLightDark
@Composable
private fun HomePreview() {
    YdsTheme {
        HomeScreen(
            studentInfo = StudentInfo(
                name = "홍길동",
                department = "컴퓨터학부",
                grade = 2,
            ),
            totalReportCardInfo = TotalReportCardInfo(
                gpa = 4.22.toGrade(),
                earnedCredit = 97.toCredit(),
                graduateCredit = 133.toCredit(),
            ),
        )
    }
}
