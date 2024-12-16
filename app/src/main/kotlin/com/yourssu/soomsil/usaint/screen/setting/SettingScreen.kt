package com.yourssu.soomsil.usaint.screen.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.yourssu.design.system.atom.Text
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.ListItem
import com.yourssu.design.system.compose.atom.TopBarButton
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.design.system.compose.component.topbar.TopBar
import com.yourssu.soomsil.usaint.R
import com.yourssu.design.R as YdsR

@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    YdsScaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                navigationIcon = {
                    TopBarButton(
                        onClick = onBackClick,
                        isDisabled = false,
                        icon = YdsR.drawable.ic_arrow_left_line,
                    )
                },
            )
        },
    ) {
        com.yourssu.design.system.compose.component.List {
            item {
                ListItem(
                    text = stringResource(id = R.string.setting_logout),
                    onClick = {
                        // viewModel.logout()
                        // activity.toast("로그아웃 되었습니다.")
                        onBackClick()
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingScreen(){
    SettingScreen(onBackClick = {})
}