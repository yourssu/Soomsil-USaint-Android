package com.yourssu.soomsil.usaint.screen.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.ListItem
import com.yourssu.design.system.compose.atom.Toggle
import com.yourssu.design.system.compose.atom.TopBarButton
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.design.system.compose.component.List
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
        Column(
            modifier = modifier
        ) {
            YdsText(
                text = stringResource(id = R.string.setting),
                style = YdsTheme.typography.title1,
                modifier = modifier.padding(start = 16.dp, top = 6.dp, bottom = 8.dp),
            )

            List(subHeader = stringResource(R.string.manage_account)) {
                item {
                    ListItem(
                        text = stringResource(id = R.string.setting_logout),
                        onClick = {
                            // TODO
                            onBackClick()
                        },
                    )
                }
            }

            List(subHeader = stringResource(R.string.alarm)) {
                item {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        YdsText(
                            text = stringResource(R.string.get_alarm),
                            style = YdsTheme.typography.body1,
                        )

                        var checked1 by remember { mutableStateOf(false) } // TODO
                        Toggle(
                            checked = checked1, // TODO
                            onCheckedChange = {
                                checked1 = it // TODO
                            },
                        )
                    }
                }
            }

            List(subHeader = stringResource(R.string.terms_title)) {
                item {
                    ListItem(
                        text = stringResource(R.string.terms_of_service),
                        onClick = {
                            // TODO
                        },
                    )

                    ListItem(
                        text = stringResource(R.string.terms_of_privacy_info),
                        onClick = {
                            // TODO
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingScreen(){
    YdsTheme{
        SettingScreen(onBackClick = {})
    }
}