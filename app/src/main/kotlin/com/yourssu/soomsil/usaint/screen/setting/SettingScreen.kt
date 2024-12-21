package com.yourssu.soomsil.usaint.screen.setting

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.ListItem
import com.yourssu.design.system.compose.atom.Toggle
import com.yourssu.design.system.compose.atom.TopBarButton
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.design.system.compose.component.List
import com.yourssu.design.system.compose.component.topbar.TopBar
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.util.TwoButtonDialog
import timber.log.Timber
import com.yourssu.design.R as YdsR

@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    navigateToWebView: (url: String) -> Unit = {},
    viewModel: SettingViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val state = viewModel.state.collectAsStateWithLifecycle().value

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { uiEvent ->
                when (uiEvent) {
                    is UiEvent.Success -> {
                        Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        navigateToLogin()
                    }

                    is UiEvent.Failure -> {
                        Toast.makeText(context, uiEvent.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    SettingScreen(
        onBackClick = onBackClick,
        navigateToWebView = navigateToWebView,
        dialogState = state.showDialog,
        clickAlarmState = state.checkAlarm,
        context = context,
        updateDialogState = viewModel::updateDialogState,
        updateAlarmState = viewModel::updateAlarmState,
        logout = viewModel::logout,
        modifier = modifier
    )
}

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    navigateToWebView: (String) -> Unit = {},
    dialogState: Boolean = false,
    clickAlarmState: Boolean = false,
    context: Context = LocalContext.current,
    updateDialogState: (Boolean) -> Unit = {},
    updateAlarmState: (Boolean) -> Unit = {},
    logout: () -> Unit = {}
) {
    // 알림 권한 요청 런처
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "알림 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            updateAlarmState(true)
        } else {
            Toast.makeText(context, "알림 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            updateAlarmState(false)
        }
    }

    YdsScaffold(
        modifier = modifier,
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
            modifier = modifier
        ) {
            YdsText(
                text = stringResource(id = R.string.setting),
                style = YdsTheme.typography.title1,
                modifier = modifier.padding(start = 16.dp, top = 6.dp, bottom = 8.dp)
            )

            List(subHeader = stringResource(R.string.manage_account)) {
                item {
                    ListItem(
                        text = stringResource(id = R.string.setting_logout),
                        onClick = {
                            updateDialogState(true)
                        },
                    )
                }
            }

            if (dialogState) {
                TwoButtonDialog(
                    title = "로그아웃 하시겠습니까?",
                    positiveButtonText = "로그아웃",
                    negativeButtonText = "취소",
                    onPositiveButtonClicked = {
                        logout()
                        updateDialogState(false)
                    },
                    onNegativeButtonClicked = {
                        updateDialogState(false)
                    },
                    negativeButtonTextColor = Color.Blue,
                    positiveButtonTextColor = Color.Red
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
                            checked = clickAlarmState,
                            onCheckedChange = {
                                // toggle을 켠 경우
                                if(it){
                                    askNotificationPermission(context, requestPermissionLauncher, updateAlarmState)
                                } else {
                                    updateAlarmState(false) // toggle을 끔
                                }
                            }
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

// 알림 권한 요청
private fun askNotificationPermission(
    context: Context,
    requestPermissionLauncher: ActivityResultLauncher<String>,
    updateAlarmState: (Boolean) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13 이상: 권한 요청 필요
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 이미 허용됨
                Timber.d("권한이 이미 허용됨")
                updateAlarmState(true)
            }

            shouldShowRequestPermissionRationale(context as ComponentActivity, Manifest.permission.POST_NOTIFICATIONS) -> {
                // 권한 요청 설명이 필요한 경우
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            else -> {
                // 사용자가 이전에 완전히 거부한 상태
                Toast.makeText(
                    context,
                    "알림 권한이 필요합니다. 설정에서 권한을 허용해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                moveToSettings(context)
            }
        }
    } else {
        // Android 13 미만: 권한 요청 불필요
        Timber.d("Android 13 미만: 권한 요청 불필요")
    }
}

// 설정 화면으로 이동
private fun moveToSettings(context: Context) {
    val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = android.net.Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}


@Preview
@Composable
fun PreviewSettingScreen() {
    YdsTheme {
        SettingScreen()
    }
}