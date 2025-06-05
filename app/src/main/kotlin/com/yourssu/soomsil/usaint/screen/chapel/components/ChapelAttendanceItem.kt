package com.yourssu.soomsil.usaint.screen.chapel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.core.model.ChapelAttendanceData

@Composable
fun ChapelAttendanceItem(
    modifier: Modifier = Modifier,
    attendanceData: ChapelAttendanceData
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = attendanceData.category,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Text(
                text = "${attendanceData.instructor} • ${attendanceData.instructorDepartment.ifBlank { "소속 없음" } }" ,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Box (
            modifier = modifier
                .align(Alignment.CenterVertically)
                .padding(10.dp)
                .clip(CircleShape)
                .background(
                    when (attendanceData.attendance) {
                        "결석" -> Color(0xFFFF244B)
                        "출석" -> Color(0xFF16B874)
                        else -> Color.Unspecified
                    }

                )
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = attendanceData.attendance,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Preview
@Composable
fun PreviewChapelAttendanceItem() {
    Column {
        ChapelAttendanceItem(
            attendanceData = ChapelAttendanceData.previewData,
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        )
        ChapelAttendanceItem(attendanceData = ChapelAttendanceData.previewData,
            modifier = Modifier.background(MaterialTheme.colorScheme.background))
    }
}