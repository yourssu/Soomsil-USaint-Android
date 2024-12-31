package com.yourssu.soomsil.usaint.screen.setting

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU) // Android 13 버전부터 알림 권한 허용 받아야 함 (POST_NOTIFICATIONS)
@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    navigateToWebView: (url: String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
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
                // 최초 알림 권한 요청
                is SettingEvent.ShowPermissionRequest -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.request_alarm_permission), Toast.LENGTH_SHORT
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                // 알림 권한 요청을 거부한 경우 설정 앱에서 직접 알림 권한 허용
                is SettingEvent.NavigateToSettings -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.request_alarm_permission_in_setting),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    ).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }

                // 로그아웃
                is SettingEvent.SuccessLogout -> {
                    Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }

                is SettingEvent.FailureLogout -> {
                    Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
                }

                // 알림 토글 클릭
                is SettingEvent.ClickToggle -> {
                    Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    SettingScreen(
        modifier = modifier,
        onBackClick = onBackClick,
        onClickTermsOfService = {
            navigateToWebView(context.resources.getString(R.string.terms_of_service_url))
        },
        onClickTermsOfPrivacy = {
            navigateToWebView(context.resources.getString(R.string.terms_of_privacy_info_url))
        },
        showDialog = state.showDialog,
        notificationToggle = state.notificationToggle,
        onShowDialogChange = viewModel::updateDialogState,
        onNotificationToggleChange = { isChecked ->
            viewModel.checkNotificationPermission(context, isChecked)
        },
        onLogout = viewModel::logout
    )
}


@Composable
fun SettingScreen(
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit,
    notificationToggle: Boolean,
    onNotificationToggleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onClickTermsOfService: () -> Unit = {},
    onClickTermsOfPrivacy: () -> Unit = {},
) {
    YdsScaffold(
        topBar = {
            TopBar(
                navigationIcon = {
                    TopBarButton(
                        onClick = onBackClick,
                        icon = YdsR.drawable.ic_arrow_left_line
                    )
                },
            )
        },
    ) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp)
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
                        onClick = { onShowDialogChange(true) }
                    )
                }
            }

            if (showDialog) {
                TwoButtonDialog(
                    title = stringResource(R.string.logout_title),
                    positiveButtonText = stringResource(R.string.logout),
                    negativeButtonText = stringResource(R.string.cancel),
                    onPositiveButtonClicked = {
                        onLogout()
                        onShowDialogChange(false)
                    },
                    onNegativeButtonClicked = { onShowDialogChange(false) },
                )
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
                            style = YdsTheme.typography.body1
                        )

                        Toggle(
                            checked = notificationToggle,
                            onCheckedChange = onNotificationToggleChange
                        )
                    }
                }
            }

            List(subHeader = stringResource(R.string.terms_title)) {
                item {
                    ListItem(
                        text = stringResource(R.string.terms_of_service),
                        onClick = onClickTermsOfService,
                    )

                    ListItem(
                        text = stringResource(R.string.terms_of_privacy_info),
                        onClick = onClickTermsOfPrivacy,
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
        SettingScreen(
            showDialog = false,
            onShowDialogChange = {},
            notificationToggle = false,
            onNotificationToggleChange = {},
        )
    }
}