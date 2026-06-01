package com.yourssu.soomsil.usaint.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.soomsil.usaint.screen.main.model.GpaBarData

@Composable
fun GpaChartCard(
    bars: List<GpaBarData>,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "전체 학기 추이",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                letterSpacing = (-0.2).sp
            )
            Text(
                text = "자세히 →",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0062FF),
                letterSpacing = (-0.2).sp,
                modifier = Modifier.clickable { onDetailClick() }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEach { bar ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 값 라벨 영역: 모든 막대에 동일한 높이를 예약해 막대 바닥선이 어긋나지 않도록 함
                    Box(
                        modifier = Modifier.height(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bar.gpaText != null) {
                            Text(
                                text = bar.gpaText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0A0A0A),
                                letterSpacing = (-0.3).sp
                            )
                        }
                    }

                    // 플롯 영역: 막대를 하단에 고정해 모든 막대가 같은 바닥선을 공유
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(bar.height)
                                .background(
                                    if (bar.isCurrent) Color(0xFF0A0A0A) else Color(0xFFDCE9FF),
                                    RoundedCornerShape(6.dp)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = bar.label,
                        fontSize = 10.sp,
                        fontWeight = if (bar.isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (bar.isCurrent) Color(0xFF0A0A0A) else Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}
