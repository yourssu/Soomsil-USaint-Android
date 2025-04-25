package com.yourssu.soomsil.usaint.screen.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.core.model.StudentData
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun StudentDataItem(
    studentData: StudentData?,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onProfileClick,
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
                    text = studentData?.name ?: "-",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight(600),
                    ),
                )
                Text(
                    text = stringResource(
                        R.string.student_department_and_grade_format,
                        studentData?.department ?: "-",
                        studentData?.grade ?: 0,
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(12.dp))
            IconButton(
                onClick = onSettingClick,
            ) {
                Icon(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp),
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "settings",
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun StudentInfoPreview() {
    SoomsilUSaintTheme {
        Surface {
            StudentDataItem(
                studentData = StudentData.previewData,
            )
        }
    }
}
