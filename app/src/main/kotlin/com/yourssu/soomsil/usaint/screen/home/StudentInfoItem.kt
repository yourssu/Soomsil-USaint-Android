package com.yourssu.soomsil.usaint.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.ui.entities.StudentInfo
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun StudentInfoItem(
    studentInfo: StudentInfo?,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        onClick = onProfileClick,
        color = Color.Transparent,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 20.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                painter = painterResource(R.drawable.ic_default_profile_image),
                contentDescription = null,
            )
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = studentInfo?.name ?: "-",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight(600),
                    ),
                )
                Text(
                    text = stringResource(
                        R.string.student_department_and_grade_format,
                        studentInfo?.department ?: "-",
                        studentInfo?.grade ?: 0,
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(12.dp))
            Icon(
                modifier = Modifier
                    .clickable(onClick = onSettingClick)
                    .padding(4.dp)
                    .size(24.dp),
                imageVector = Icons.Outlined.Settings,
                contentDescription = "settings",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StudentInfoPreview() {
    SoomsilUSaintTheme {
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
