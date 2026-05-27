package com.yourssu.soomsil.usaint.screen.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.soomsil.usaint.BuildConfig
import com.yourssu.soomsil.usaint.ui.components.TabBar

@Composable
@Preview()
fun MyPageScreen(
    onBackClick: () -> Unit = {},
    tabBar: @Composable () -> Unit = {}
) {
    MyPageScreenContent(tabBar = tabBar)
}


// ─── Header ───

@Composable
private fun SettingsHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal =20.dp),
        contentAlignment = Alignment.CenterStart ) {
        Text(
            text = "설정",
            fontSize =24.sp,
            lineHeight =30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191F28)
        )
    }
}

// ─── Section Title ───

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF191F28)
    )
}

// ─── Setting Row (텍스트만) ───

@Composable
private fun SettingRow(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF4E5968)
        )
    }
}

// ─── Toggle Row ───

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF4E5968)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF775EFF),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD1D5DB)
            )
        )
    }
}

// ─── Section ───

@Composable
private fun SettingSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(title)
        content()
    }
}

// ─── Screen ───

@Composable
private fun MyPageScreenContent(
    tabBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    var gradeNotification by remember { mutableStateOf(true) }
    var campusNotification by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SettingsHeader()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // 계정관리
                SettingSection("계정관리") {
                    SettingRow("로그아웃")
                }

                // 알림
                SettingSection("알림") {
                    ToggleRow(
                        label = "성적 알림 받기",
                        checked = gradeNotification,
                        onCheckedChange = { gradeNotification = it }
                    )
                    ToggleRow(
                        label = "캠퍼스 알림 받기",
                        checked = campusNotification,
                        onCheckedChange = { campusNotification = it }
                    )
                }

                // 약관
                SettingSection("약관") {
                    SettingRow("이용약관")
                    SettingRow("개인정보 처리 방침")
                }

                // 버전정보
                SettingSection("버전정보") {
                    SettingRow("v ${BuildConfig.VERSION_NAME}")
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
        ) {
            tabBar()
        }
    }
}