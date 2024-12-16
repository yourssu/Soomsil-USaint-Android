package com.yourssu.soomsil.saint.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.BoxButton
import com.yourssu.design.system.compose.atom.BoxButtonSize
import com.yourssu.design.system.compose.atom.BoxButtonType
import com.yourssu.design.system.compose.atom.Divider
import com.yourssu.design.system.compose.atom.ProfileImageView
import com.yourssu.design.system.compose.atom.Thickness
import com.yourssu.design.system.compose.base.Icon
import com.yourssu.design.system.compose.base.Surface
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.design.system.compose.base.ydsClickable
import com.yourssu.design.system.compose.component.topbar.SingleTitleTopBar
import com.yourssu.soomsil.usaint.R
import com.yourssu.design.R as YdsR


private val DisabledAlpha = 0.5f

//@Composable
//internal fun SaintHomeScreen(
//    modifier: Modifier = Modifier,
//    onProfileClick: () -> Unit = {},
//    onSettingClick: () -> Unit = {},
//    onGradeCardClick: () -> Unit = {},
//    onChapelCardClick: () -> Unit = {},
//    onGraduationCardClick: () -> Unit = {},
////    viewModel: SaintHomeViewModel = hiltViewModel(),
//) {
//    val isLoggedIn by remember(viewModel.userName, viewModel.userInfo) {
//        mutableStateOf(viewModel.userName != null && viewModel.userInfo != null)
//    }
//    LaunchedEffect(Unit) {
//        viewModel.update()
//    }
//
//    SaintHomeScreen(
//        isLoggedIn = isLoggedIn,
//        userName = viewModel.userName ?: stringResource(id = R.string.user_name_placeholder),
//        userInfo = viewModel.userInfo ?: stringResource(id = R.string.user_info_placeholder),
//        modifier = modifier,
//        onProfileClick = onProfileClick,
//        onSettingClick = onSettingClick,
//        onGradeCardClick = onGradeCardClick,
//        onChapelCardClick = onChapelCardClick,
//        onGraduationCardClick = onGraduationCardClick,
//    )
//}

@Composable
internal fun SaintHomeScreen(
    isLoggedIn: Boolean,
    userName: String,
    userInfo: String,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onGradeCardClick: () -> Unit = {},
    onChapelCardClick: () -> Unit = {},
    onGraduationCardClick: () -> Unit = {},
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
            ProfileItem(
                isLoggedIn = isLoggedIn,
                userName = userName,
                userInfo = userInfo,
                onProfileClick = onProfileClick,
                onSettingClick = {
                    if (isLoggedIn) onSettingClick() else onProfileClick()
                },
            )
            Spacer(Modifier.height(12.dp))
            GradeCard(
                isLoggedIn = isLoggedIn,
                onGradeCardClick = onGradeCardClick,
            )
            Spacer(Modifier.height(12.dp))
            ChapelCard(
                isLoggedIn = isLoggedIn,
                onChapelCardClick = onChapelCardClick,
            )
            Spacer(Modifier.height(12.dp))
            GraduationCard(
                isLoggedIn = isLoggedIn,
                onGraduationCardClick = onGraduationCardClick,
            )
        }
    }
}

@Composable
private fun ProfileItem(
    isLoggedIn: Boolean,
    userName: String,
    userInfo: String,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .ydsClickable(
                interactionSource = remember { MutableInteractionSource() },
            ) {
                if (!isLoggedIn) onProfileClick()
            }
            .padding(
                horizontal = 16.dp,
                vertical = 20.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileImageView(R.drawable.ic_default_profile_image)
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            YdsText(
                text = userName,
                style = YdsTheme.typography.subTitle1.copy(
                    fontWeight = FontWeight(600),
                ),
            )
            YdsText(
                text = userInfo,
                style = YdsTheme.typography.body2,
                color = YdsTheme.colors.textSecondary,
            )
        }
        Spacer(Modifier.width(12.dp))
        Icon(
            modifier = Modifier.ydsClickable(
                interactionSource = remember { MutableInteractionSource() },
                onClick = onSettingClick,
            ),
            id = YdsR.drawable.ic_setting_line,
        )
    }
}

@Composable
private fun GradeCard(
    isLoggedIn: Boolean,
    onGradeCardClick: () -> Unit,
) {
    Surface(
        rounding = 8.dp,
        color = YdsTheme.colors.bgNormal,
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
                    alpha = if (isLoggedIn) 1f else DisabledAlpha,
                ),
            )
            ActionTitle(
                title = stringResource(id = R.string.saint_grade_title),
                subTitle = stringResource(id = R.string.saint_grade_subtitle),
                onClick = onGradeCardClick,
                enable = isLoggedIn,
            )
////            GradeSummary(onGradeCardClick)
        }
    }
}

@Composable
private fun GradeSummary(onGradeCardClick: () -> Unit) {
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
private fun ChapelCard(
    isLoggedIn: Boolean,
    onChapelCardClick: () -> Unit,
) {
    Surface(
        rounding = 8.dp,
        color = YdsTheme.colors.bgNormal,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        ) {
            YdsText(
                text = stringResource(id = R.string.saint_chapel),
                modifier = Modifier
                    .padding(
                        top = 20.dp,
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                style = YdsTheme.typography.title3,
                color = YdsTheme.colors.textPrimary.copy(
                    alpha = if (isLoggedIn) 1f else DisabledAlpha,
                ),
            )
            ActionTitle(
                title = stringResource(id = R.string.saint_chapel_seat_title),
                subTitle = stringResource(id = R.string.saint_chapel_seat_subtitle),
                onClick = onChapelCardClick,
                enable = false,
//                enable = isLoggedIn,
            )
            ActionTitle(
                title = stringResource(id = R.string.saint_chapel_attendance_title),
                subTitle = stringResource(id = R.string.saint_chapel_attendance_subtitle),
                onClick = onChapelCardClick,
                enable = false,
//                enable = isLoggedIn,
            )
        }
    }
}

@Composable
private fun GraduationCard(
    isLoggedIn: Boolean,
    onGraduationCardClick: () -> Unit,
) {
    Surface(
        rounding = 8.dp,
        color = YdsTheme.colors.bgNormal,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        ) {
            YdsText(
                text = stringResource(id = R.string.saint_graduation),
                modifier = Modifier
                    .padding(
                        top = 20.dp,
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                style = YdsTheme.typography.title3,
                color = YdsTheme.colors.textPrimary.copy(
                    alpha = if (isLoggedIn) 1f else DisabledAlpha,
                ),
            )
            ActionTitle(
                title = stringResource(id = R.string.saint_graduation_title),
                subTitle = stringResource(id = R.string.saint_graduation_subtitle),
                onClick = onGraduationCardClick,
                enable = false,
//                enable = isLoggedIn,
            )
        }
    }
}

@Composable
private fun ActionTitle(
    title: String,
    subTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
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
                onClick = { if (enable) onClick() },
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
                color = YdsTheme.colors.textSecondary.copy(
                    alpha = if (enable) 1f else DisabledAlpha,
                ),
            )
            YdsText(
                text = title,
                style = YdsTheme.typography.subTitle1,
                color = YdsTheme.colors.textPrimary.copy(
                    alpha = if (enable) 1f else DisabledAlpha,
                ),
            )
        }
        Icon(
            id = YdsR.drawable.ic_arrow_right_line,
            tint = YdsTheme.colors.textPrimary.copy(
                alpha = if (enable) 1f else DisabledAlpha,
            ),
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


@PreviewLightDark
@Composable
private fun SaintHomePreview() {
    YdsTheme {
        SaintHomeScreen(
            isLoggedIn = true,
            userName = "테스트",
            userInfo = "컴퓨터학부 2학년",
        )
    }
}

@PreviewLightDark
@Composable
private fun SaintHomePreviewLogout() {
    YdsTheme {
        SaintHomeScreen(
            isLoggedIn = false,
            userName = "테스트",
            userInfo = "컴퓨터학부 2학년",
        )
    }
}
