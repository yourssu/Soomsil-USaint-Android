package com.yourssu.soomsil.usaint.screen.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.ui.components.TabBar

@Composable
@Preview
fun MainScreen(){
    MainPageScreen(tabBar = { TabBar() })
}
// ─── Header ───

@Composable
fun MainHeader(
    greetingName: String,
    notificationCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .heightIn(min = 64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "안녕하세요, ${greetingName}님",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                letterSpacing = (-0.4).sp
            )
            Text(
                text = "이번 주 확인할 알림 ${notificationCount}개",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

// ─── Profile Card ───

@Composable
fun ProfileCard(
    name: String,
    department: String,
    year: String,
    status: String,
    studentId: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                letterSpacing = (-0.3).sp
            )
            Text(
                text = "$department · $year · $status",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF4B5563)
            )
        }
        Box(
            modifier = Modifier
                .background(Color(0x140A0A0A), RoundedCornerShape(9999.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "학번 $studentId",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0A0A0A)
            )
        }
    }
}

// ─── GPA Hero Card ───

@Composable
fun GpaHeroCard(
    gpa: String,
    maxGpa: String,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0A), RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 상단 라벨
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "내 성적 ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xCCFFFFFF)
            )
            Text(
                text = "전체",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0x80FFFFFF)
            )
        }

        // GPA 숫자
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = gpa,
                fontSize = 80.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-5).sp
            )
            Text(
                text = "/ $maxGpa",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0x80FFFFFF)
            )
        }

        // CTA 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(9999.dp))
                .clickable { onDetailClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "이번 학기 성적보기",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0A0A0A)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                painter = painterResource(R.drawable.ic_tabbar_bell), // was ic_arrow_right
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF0A0A0A)
            )
        }
    }
}

// ─── GPA Chart Card ───

data class GpaBarData(
    val label: String,
    val height: Dp,
    val isCurrent: Boolean = false,
    val gpaText: String? = null
)

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
        // 헤더
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

        // 바 차트
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(118.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEach { bar ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    if (bar.gpaText != null) {
                        Text(
                            text = bar.gpaText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A0A0A),
                            letterSpacing = (-0.3).sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(bar.height)
                            .background(
                                if (bar.isCurrent) Color(0xFF0A0A0A) else Color(0xFFDCE9FF),
                                RoundedCornerShape(6.dp)
                            )
                    )

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

// ─── Chapel Card ───

@Composable
fun ChapelCard(
    attended: Int,
    total: Int,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "채플 출석",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "${attended}/${total}회 출석",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0062FF)
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_tabbar_bell), // was ic_chevron_right
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color(0xFF9CA3AF)
            )
        }

        // 프로그레스 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(Color(0xFFF1F5F9))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(Color(0xFF0062FF), RoundedCornerShape(9999.dp))
            )
        }
    }
}

// ─── Tab Bar ───

data class TabItem(
    val label: String,
    @DrawableRes val iconRes: Int
)

@Composable
fun PillTabBar(
    selectedIndex: Int,
    tabs: List<TabItem>,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White, RoundedCornerShape(9999.dp))
                .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(9999.dp))
                .padding(6.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (isSelected) Color(0xFF0062FF) else Color.Transparent,
                            RoundedCornerShape(9999.dp)
                        )
                        .clickable { onTabClick(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                ) {
                    Icon(
                        painter = painterResource(tab.iconRes),
                        contentDescription = tab.label,
                        modifier = Modifier.size(18.dp),
                        tint = if (isSelected) Color.White else Color(0xFF9CA3AF)
                    )
                    Text(
                        text = tab.label,
                        fontSize = if (isSelected) 12.sp else 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}

// ─── Screen ───

@Composable
fun MainPageScreen(
    greetingName: String = "강우현",
    notificationCount: Int = 3,
    profileName: String = "강우현",
    department: String = "컴퓨터학부",
    year: String = "2학년",
    status: String = "재학",
    studentId: String = "20231234",
    gpa: String = "3.87",
    maxGpa: String = "4.5",
    barData: List<GpaBarData> = listOf(
        GpaBarData("1-1", 40.dp),
        GpaBarData("1-2", 50.dp),
        GpaBarData("2-1", 46.dp),
        GpaBarData("2-2", 60.dp),
        GpaBarData("3-1", 80.dp, isCurrent = true, gpaText = "4.21")
    ),
    chapelAttended: Int = 5,
    chapelTotal: Int = 8,
    chapelProgress: Float = 0.625f,
    tabs: List<TabItem> = listOf(
        TabItem("홈", R.drawable.ic_tabbar_bell), // was ic_house
        TabItem("채플", R.drawable.ic_tabbar_bell), // was ic_armchair
        TabItem("알림", R.drawable.ic_tabbar_bell), // was ic_bell
        TabItem("마이", R.drawable.ic_tabbar_bell) // was ic_user
    ),
    selectedTabIndex: Int = 0,
    onGradeDetailClick: () -> Unit = {},
    onChartDetailClick: () -> Unit = {},
    onChapelClick: () -> Unit = {},
    onTabClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    tabBar: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        MainHeader(
            greetingName = greetingName,
            notificationCount = notificationCount
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileCard(
                name = profileName,
                department = department,
                year = year,
                status = status,
                studentId = studentId
            )
            GpaHeroCard(
                gpa = gpa,
                maxGpa = maxGpa,
                onDetailClick = onGradeDetailClick
            )
            GpaChartCard(
                bars = barData,
                onDetailClick = onChartDetailClick
            )
            ChapelCard(
                attended = chapelAttended,
                total = chapelTotal,
                progress = chapelProgress,
                onClick = onChapelClick
            )
        }

        tabBar()
    }
}