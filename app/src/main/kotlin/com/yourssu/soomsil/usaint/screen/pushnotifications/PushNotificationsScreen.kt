package com.yourssu.soomsil.usaint.screen.pushnotifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.soomsil.usaint.ui.components.TabBar


@Composable
@Preview
fun PushNotificationsScreen() {
    PushNotificationsScreen({ TabBar() })
}

// ─── Data Models ───

enum class NotificationCategory(
    val label: String,
    val dotColor: Color,
    val labelColor: Color = Color(0xFF6B7280)
) {
    ACADEMIC("학사", Color(0xFF775EFF)),
    NEWS("소식", Color(0xFFF59E0B)),
    GRADE("성적", Color(0xFF22C55E)),
    CLASS("수업", Color(0xFF3B82F6)),
    EMERGENCY("긴급", Color(0xFFF04452), labelColor = Color(0xFFF04452)),
    NOTICE("공지", Color(0xFF8B95A1), labelColor = Color(0xFF8B95A1))
}

data class NotificationItem(
    val category: NotificationCategory,
    val time: String,
    val message: String,
    val isEmergency: Boolean = false,
    val isRead: Boolean = false
)

// ─── Header ───

@Composable
fun NotificationHeader(modifier: Modifier = Modifier) {
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "알림",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191F28)
        )
    }
}

// ─── Notification Card ───

@Composable
fun NotificationCard(
    item: NotificationItem,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (item.isEmergency) Color(0xFFFEF2F2) else Color.Transparent
    val borderColor = if (item.isEmergency) Color(0xFFFEE2E2) else Color.Transparent
    val cornerRadius = if (item.isEmergency) 14.dp else 0.dp
    val horizontalPadding = if (item.isEmergency) 16.dp else 0.dp
    val alpha = if (item.isRead) 0.6f else 1f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha }
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .then(
                if (item.isEmergency) Modifier.border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
                else Modifier
            )
            .padding(vertical = 12.dp, horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(item.category.dotColor, CircleShape)
                )
                Text(
                    text = item.category.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = item.category.labelColor
                )
            }
            Text(
                text = item.time,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB0B8C1)
            )
        }

        Text(
            text = item.message,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF191F28),
            lineHeight = (14 * 1.4).sp
        )
    }
}

// ─── Helpers ───

@Composable
fun NotificationDivider() {
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF2F4F6))
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF8B95A1)
    )
}

// ─── Screen ───

@Composable
fun PushNotificationsScreen(
    tabBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayNotifications = listOf(
        NotificationItem(NotificationCategory.ACADEMIC, "오전 8:00", "오늘의 시간표를 확인해요"),
        NotificationItem(NotificationCategory.NEWS, "오전 10:30", "중간고사를 끝낸 당신! 행운 복권의 기회를 드려요."),
        NotificationItem(NotificationCategory.GRADE, "오후 2:15", "새로운 성적이 등록되었어요. 확인해 주세요."),
        NotificationItem(NotificationCategory.CLASS, "오후 1:45", "수업시간이 얼마 남지 않았어요. 수업을 준비해주세요."),
        NotificationItem(NotificationCategory.EMERGENCY, "오후 5:00", "서버에 오류가 발생했어요. 점검 후 돌아올게요!", isEmergency = true)
    )

    val previousNotifications = listOf(
        NotificationItem(NotificationCategory.NOTICE, "어제", "3월 학사일정 안내가 업데이트되었습니다.", isRead = true)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        NotificationHeader()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 20.dp)
        ) {
            SectionLabel("오늘")

            todayNotifications.forEachIndexed { index, item ->
                NotificationCard(item = item)
                if (index < todayNotifications.lastIndex) {
                    NotificationDivider()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SectionLabel("이전")

            previousNotifications.forEach { item ->
                NotificationCard(item = item)
            }
        }

        tabBar()
    }
}