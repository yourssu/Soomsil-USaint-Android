package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.ProfileImageView
import com.yourssu.design.system.compose.base.Icon
import com.yourssu.design.system.compose.base.Surface
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.design.system.compose.base.ydsClickable
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.ui.entities.StudentInfo


@Composable
fun StudentInfoItem(
    studentInfo: StudentInfo,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .ydsClickable(onClick = onProfileClick)
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
                text = studentInfo.name,
                style = YdsTheme.typography.subTitle1.copy(
                    fontWeight = FontWeight(600),
                ),
            )
            YdsText(
                text = stringResource(
                    R.string.student_department_and_grade_format,
                    studentInfo.department, studentInfo.grade
                ),
                style = YdsTheme.typography.body2,
                color = YdsTheme.colors.textSecondary,
            )
        }
        Spacer(Modifier.width(12.dp))
        Icon(
            modifier = Modifier.ydsClickable(onClick = onSettingClick),
            id = com.yourssu.design.R.drawable.ic_setting_line,
        )
    }
}

@PreviewLightDark
@Composable
private fun StudentInfoPreview() {
    YdsTheme {
        Surface {
            StudentInfoItem(
                studentInfo = StudentInfo(
                    name = "홍길동",
                    department = "학부이름",
                    grade = 3,
                )
            )
        }
    }
}
