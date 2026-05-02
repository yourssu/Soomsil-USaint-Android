package com.yourssu.soomsil.usaint.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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

@Composable
fun TabBar() {
    TabBarContent()
}

@Composable
@Preview
private fun TabBarContent(){
    val activeColor = Color(0xFF775EFF)
    val inactiveIconColor = Color(0xFFD1D5DB)
    val inactiveLabelColor = Color(0xFFB0B8C1)
    val activeTextColor = Color(0xFFFFFFFF)

    val buttonColor = ButtonDefaults.buttonColors(
        disabledContentColor = inactiveIconColor,
        disabledContainerColor = Color.Transparent,
        contentColor = activeTextColor,
        containerColor = activeColor
    )
    BottomAppBar(
        modifier = Modifier
            .padding(horizontal = 21.dp, vertical = 12.dp)
            .height(62.dp)
            .border(BorderStroke(1.dp, Color(0xFFF3F4F6)), shape = CircleShape)
            .clip(CircleShape),
        containerColor = Color(0xFFFFFFFF),
        contentPadding = PaddingValues(0.dp)
    ){
        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TabItem(
                label = "홈",
                iconId = R.drawable.ic_tabbar_house,
                isActive = true,
                buttonColor = buttonColor,
                activeTextColor = activeTextColor,
                inactiveIconColor = inactiveIconColor,
                inactiveLabelColor = inactiveLabelColor
            )
            TabItem(
                label = "소식",
                iconId = R.drawable.ic_tabbar_megaphone,
                isActive = false,
                buttonColor = buttonColor,
                activeTextColor = activeTextColor,
                inactiveIconColor = inactiveIconColor,
                inactiveLabelColor = inactiveLabelColor
            )
            TabItem(
                label = "알림",
                iconId = R.drawable.ic_tabbar_bell,
                isActive = false,
                buttonColor = buttonColor,
                activeTextColor = activeTextColor,
                inactiveIconColor = inactiveIconColor,
                inactiveLabelColor = inactiveLabelColor
            )
            TabItem(
                label = "마이",
                iconId = R.drawable.ic_tabbar_user,
                isActive = false,
                buttonColor = buttonColor,
                activeTextColor = activeTextColor,
                inactiveIconColor = inactiveIconColor,
                inactiveLabelColor = inactiveLabelColor
            )
        }
    }
}

@Composable
private fun RowScope.TabItem(
    label: String,
    iconId: Int,
    isActive: Boolean,
    buttonColor: ButtonColors,
    activeTextColor: Color,
    inactiveIconColor: Color,
    inactiveLabelColor: Color
) {
    Button(
        onClick = { },
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(26.dp)),
        colors = if (isActive) {
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFF775EFF),
                contentColor = activeTextColor
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = inactiveIconColor
            )
        }
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = label,
                modifier = Modifier.size(18.dp),
                colorFilter = ColorFilter.tint(
                    color = if (isActive) activeTextColor else inactiveIconColor,
                    blendMode = BlendMode.SrcIn
                )
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight(600),
                color = if (isActive) activeTextColor else inactiveLabelColor
            )
        }

    }
}
