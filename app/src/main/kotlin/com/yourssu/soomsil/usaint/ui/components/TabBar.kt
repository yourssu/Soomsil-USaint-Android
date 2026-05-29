package com.yourssu.soomsil.usaint.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.ui.components.navigation.TabBarDestination
import com.yourssu.soomsil.usaint.screen.chapel.navigation.Chapel
import com.yourssu.soomsil.usaint.screen.main.navigation.Main
import com.yourssu.soomsil.usaint.screen.mypage.navigation.MyPage
import com.yourssu.soomsil.usaint.screen.pushnotifications.navigation.PushNotifications

@Composable
fun TabBar(
    modifier: Modifier = Modifier,
    items: List<TabBarDestination> = TabBarDefaults.items,
    selectedRoute: String? = items.firstOrNull()?.route,
    onItemSelected: (TabBarDestination) -> Unit = {},
) {
    TabBarContent(
        items = items,
        selectedRoute = selectedRoute,
        onItemSelected = onItemSelected,
        modifier = modifier,
    )
}

object TabBarDefaults {
    val items = listOf(
        TabBarDestination(
            route = requireNotNull(Main::class.qualifiedName),
            label = "홈",
            iconId = R.drawable.ic_tabbar_house
        ),
        TabBarDestination(
            route = requireNotNull(Chapel::class.qualifiedName),
            label = "채플",
            iconId = R.drawable.ic_tabbar_megaphone
        ),
        TabBarDestination(
            route = requireNotNull(PushNotifications::class.qualifiedName),
            label = "알림",
            iconId = R.drawable.ic_tabbar_bell
        ),
        TabBarDestination(
            route = requireNotNull(MyPage::class.qualifiedName),
            label = "마이",
            iconId = R.drawable.ic_tabbar_user
        ),
    )
}

@Composable
@Preview
private fun TabBarContent(
    modifier: Modifier = Modifier,
    items: List<TabBarDestination> = TabBarDefaults.items,
    selectedRoute: String? = items.firstOrNull()?.route,
    onItemSelected: (TabBarDestination) -> Unit = {},
){
    val activeColor = Color(0xFF0062FF)
    val inactiveColor = Color(0xFFA1A1A1)
    val borderColor = Color(0xFFF1F5F9)

    Box(
        modifier = modifier
            .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .height(62.dp)
                .fillMaxWidth()
                .border(1.dp, borderColor, CircleShape)
                .clip(CircleShape)
                .background(Color(0xFFFFFFFF))
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                TabItem(
                    label = item.label,
                    iconId = item.iconId,
                    isActive = item.route == selectedRoute,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onItemSelected(item) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.TabItem(
    label: String,
    iconId: Int,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit,
) {
    val contentColor = if (isActive) Color(0xFFFFFFFF) else inactiveColor
    Surface(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        shape = RoundedCornerShape(9999.dp),
        color = if (isActive) activeColor else Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = label,
                modifier = Modifier.size(18.dp),
                colorFilter = ColorFilter.tint(
                    color = contentColor,
                    blendMode = BlendMode.SrcIn
                )
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight(500),
                color = contentColor
            )
        }
    }
}
