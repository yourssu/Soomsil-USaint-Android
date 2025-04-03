package com.yourssu.soomsil.usaint.screen.setting

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.BuildConfig
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import com.yourssu.soomsil.usaint.util.NotificationUtil
import com.yourssu.soomsil.usaint.util.TwoButtonDialog

@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    navigateToWebView: (url: String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    // 알림 권한 요청 런처
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateNotificationState(isGranted)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                // 로그아웃
                is SettingEvent.SuccessLogout -> {
                    Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }

                is SettingEvent.FailureLogout -> {
                    Toast.makeText(context, "로그아웃을 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }

                // 알림 토글 클릭
                is SettingEvent.ClickToggle -> {
                    Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
                }

                else -> {}
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
        onNotificationToggleChange = a@{ isChecked ->
            if (!isChecked) {
                viewModel.updateNotificationState(false)
                return@a
            }
            // Android 13 미만은 권한 요청 불필요
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                viewModel.updateNotificationState(true)
                return@a
            }
            when {
                NotificationUtil.areNotificationEnabled(context) ->
                    viewModel.updateNotificationState(true)

                // 알림 권한 요청을 한 번 거부한 경우
                NotificationUtil.shouldShowRationale(context) -> {
                    // TODO: show rationale
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    Toast.makeText(
                        context, R.string.request_alarm_permission_in_setting, Toast.LENGTH_SHORT
                    ).show()
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        context.startActivity(this)
                    }
                }
            }
        },
        onLogout = viewModel::logout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.setting)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) {
        innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            TitleText(stringResource(R.string.manage_account))
            ContentText(
                text = stringResource(id = R.string.setting_logout),
                onClick = { onShowDialogChange(true) }
            )

            TitleText(stringResource(R.string.alarm))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.get_alarm)
                )
                Switch(
                    checked = notificationToggle,
                    onCheckedChange = onNotificationToggleChange
                )
            }

            TitleText(stringResource(R.string.terms_title))
            ContentText(
                text = stringResource(R.string.terms_of_service),
                onClick = onClickTermsOfService
            )

            ContentText(
                text = stringResource(R.string.terms_of_privacy_info),
                onClick = onClickTermsOfPrivacy
            )

            TitleText("버전 정보")
            ContentText("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
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
    }
}

@Composable
fun TitleText(
    text : String
) {
    Box(
        modifier = Modifier
            .height(42.dp)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ContentText(
    text: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .height(42.dp)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.clickable { onClick() },
            text = text,
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewSettingScreen() {
    var showDialog by remember { mutableStateOf(false) }
    SoomsilUSaintTheme {
        SettingScreen(
            showDialog = showDialog,
            onShowDialogChange = { showDialog = it },
            notificationToggle = false,
            onNotificationToggleChange = {},
        )
    }
}
