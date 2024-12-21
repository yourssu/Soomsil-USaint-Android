package com.yourssu.soomsil.usaint.screen.setting

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.ListItem
import com.yourssu.design.system.compose.atom.Toggle
import com.yourssu.design.system.compose.atom.TopBarButton
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.design.system.compose.component.List
import com.yourssu.design.system.compose.component.topbar.TopBar
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.util.TwoButtonDialog
import com.yourssu.design.R as YdsR
import com.yourssu.design.system.compose.component.toast.Toast

@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    navigateToWebView: (url: String) -> Unit = {},
    navigateToLogin: () -> Unit = {},
    viewModel: SettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state.collectAsStateWithLifecycle().value

    // 알림 권한 요청 런처
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.handlePermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SettingEvent.ShowPermissionRequest -> {
                    Toast.makeText(context, "알림 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                is SettingEvent.NavigateToSettings -> {
                    Toast.makeText(context, "설정 화면에서 알림 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                    val intent = android.content.Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    ).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
                is SettingEvent.SuccessLogout -> {
                    Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }
                is SettingEvent.ClickToggle -> {
                    Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    SettingScreen(
        onBackClick = onBackClick,
        navigateToWebView = navigateToWebView,
        dialogState = state.showDialog,
        clickAlarmState = state.checkAlarm,
        onDialogStateChange = viewModel::updateDialogState,
        onAlarmToggleChange = { isChecked ->
            viewModel.checkNotificationPermission(context, isChecked)
        },
        onLogout = viewModel::logout
    )
}


@Composable
fun SettingScreen(
    onBackClick: () -> Unit = {},
    navigateToWebView: (String) -> Unit = {},
    dialogState: Boolean = false,
    clickAlarmState: Boolean = false,
    onDialogStateChange: (Boolean) -> Unit = {},
    onAlarmToggleChange: (Boolean) -> Unit = {},
    onLogout: () -> Unit  = {},
    context: Context = LocalContext.current
) {
    YdsScaffold(
        topBar = {
            TopBar(
                navigationIcon = {
                    TopBarButton(
                        onClick = onBackClick,
                        isDisabled = false,
                        icon = YdsR.drawable.ic_arrow_left_line
                    )
                },
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            YdsText(
                text = stringResource(id = R.string.setting),
                style = YdsTheme.typography.title1,
                modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
            )

            List(subHeader = stringResource(R.string.manage_account)) {
                item {
                    ListItem(
                        text = stringResource(id = R.string.setting_logout),
                        onClick = { onDialogStateChange(true) }
                    )
                }
            }

            if (dialogState) {
                TwoButtonDialog(
                    title = "로그아웃 하시겠습니까?",
                    positiveButtonText = "로그아웃",
                    negativeButtonText = "취소",
                    onPositiveButtonClicked = {
                        onLogout()
                        onDialogStateChange(false)
                    },
                    onNegativeButtonClicked = { onDialogStateChange(false) },
                )
            }

            List(subHeader = stringResource(R.string.alarm)) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        YdsText(
                            text = stringResource(R.string.get_alarm),
                            style = YdsTheme.typography.body1
                        )

                        Toggle(
                            checked = clickAlarmState,
                            onCheckedChange = onAlarmToggleChange
                        )
                    }
                }
            }

            List(subHeader = stringResource(R.string.terms_title)) {
                item {
                    ListItem(
                        text = stringResource(R.string.terms_of_service),
                        onClick = {
                            navigateToWebView(context.getString(R.string.terms_of_service_url))
                        }
                    )

                    ListItem(
                        text = stringResource(R.string.terms_of_privacy_info),
                        onClick = {
                            navigateToWebView(context.getString(R.string.terms_of_privacy_info_url))
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingScreen() {
    YdsTheme {
        SettingScreen()
    }
}