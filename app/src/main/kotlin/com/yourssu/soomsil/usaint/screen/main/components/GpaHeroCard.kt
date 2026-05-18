package com.yourssu.soomsil.usaint.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.soomsil.usaint.R

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
