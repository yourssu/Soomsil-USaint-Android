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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yourssu.soomsil.usaint.BuildConfig
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import com.yourssu.soomsil.usaint.util.NotificationUtil

@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    navigateToWebView: (url: String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val settingUiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 알림 권한 요청 런처
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateNotificationSetting(isGranted)
    }

    SettingScreen(
        modifier = modifier,
        settingUiState = settingUiState,
        onBackClick = onBackClick,
        onClickTermsOfService = {
            navigateToWebView(context.resources.getString(R.string.terms_of_service_url))
        },
        onClickTermsOfPrivacy = {
            navigateToWebView(context.resources.getString(R.string.terms_of_privacy_info_url))
        },
        onNotificationToggleChange = a@{ isChecked ->
            if (!isChecked) {
                viewModel.updateNotificationSetting(false)
                return@a
            }
            // Android 13 미만은 권한 요청 불필요
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                viewModel.updateNotificationSetting(true)
                return@a
            }
            when {
                NotificationUtil.areNotificationEnabled(context) ->
                    viewModel.updateNotificationSetting(true)

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
        onSpecifiedCurrentSemester = viewModel::specifyCurrentSemester,
        onUnspecifiedCurrentSemester = viewModel::unspecifiedCurrentSemester,
        onAutoFetchToggleChange = viewModel::updateAutoFetchEnabled,
        onChangePassword = viewModel::changePassword,
        onLogout = {
            viewModel.logout()
            navigateToLogin()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    settingUiState: SettingUiState,
    @Suppress("unused") onNotificationToggleChange: (Boolean) -> Unit,
    onAutoFetchToggleChange: (Boolean) -> Unit,
    onSpecifiedCurrentSemester: (year: Int, semester: SemesterType) -> Unit,
    onUnspecifiedCurrentSemester: () -> Unit,
    onChangePassword: (password: String) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onClickTermsOfService: () -> Unit = {},
    onClickTermsOfPrivacy: () -> Unit = {},
) {
    var showCurrentSemesterSettingDialog by rememberSaveable { mutableStateOf(false) }
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var showPasswordChangeDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "설정") },
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
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Spacer(Modifier.height(8.dp))
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (settingUiState is SettingUiState.UserEditableSettings)
                            onAutoFetchToggleChange(!settingUiState.autoFetchEnabled)
                    },
                headlineContent = { Text(text = "최신 학사 정보 자동으로 불러오기") },
                trailingContent = {
                    when (settingUiState) {
                        is SettingUiState.UserEditableSettings -> {
                            Switch(
                                checked = settingUiState.autoFetchEnabled,
                                onCheckedChange = onAutoFetchToggleChange,
                            )
                        }

                        is SettingUiState.Loading -> {
                            CircularProgressIndicator()
                        }
                    }
                }
            )
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (settingUiState is SettingUiState.UserEditableSettings) {
                            if (settingUiState.currentSemesterSpecified) {
                                onUnspecifiedCurrentSemester()
                            } else {
                                showCurrentSemesterSettingDialog = true
                            }
                        }
                    },
                headlineContent = { Text(text = "현재 학기 설정") },
                supportingContent = {
                    if (settingUiState is SettingUiState.UserEditableSettings) {
                        val semesterStr =
                            settingUiState.specifiedCurrentSemester?.let { (year, semester) ->
                                "${year}년 ${semester.kor}학기"
                            } ?: "-"
                        Text(
                            text = if (settingUiState.currentSemesterSpecified) {
                                "설정된 학기: $semesterStr"
                            } else {
                                "현재 학기를 수동으로 설정할 수 있습니다.\n감지된 현재 학기: $semesterStr"
                            }
                        )
                    }
                },
                trailingContent = {
                    when (settingUiState) {
                        is SettingUiState.UserEditableSettings -> {
                            Switch(
                                checked = settingUiState.currentSemesterSpecified,
                                onCheckedChange = {
                                    if (settingUiState.currentSemesterSpecified) {
                                        onUnspecifiedCurrentSemester()
                                    } else {
                                        showCurrentSemesterSettingDialog = true
                                    }
                                },
                            )
                        }

                        is SettingUiState.Loading -> {
                            CircularProgressIndicator()
                        }
                    }
                }
            )

            Spacer(Modifier.height(16.dp))
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "계정관리",
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(8.dp))
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPasswordChangeDialog = true },
                headlineContent = { Text(text = "비밀번호 변경") }
            )
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutDialog = true },
                headlineContent = { Text(text = "로그아웃") }
            )

            /*
            알림 설정 관련

            Spacer(Modifier.height(16.dp))
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "알림",
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(8.dp))
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (settingUiState !is SettingUiState.Loading)
                            onNotificationToggleChange(!notificationEnabled)
                    },
                headlineContent = { Text(text = "알림 받기") },
                trailingContent = {
                    when (settingUiState) {
                        is SettingUiState.UserEditableSettings -> {
                            Switch(
                                checked = settingUiState.notificationEnabled,
                                onCheckedChange = onNotificationToggleChange,
                            )
                        }

                        is SettingUiState.Loading -> {
                            CircularProgressIndicator()
                        }
                    }
                }
            )
            */

            Spacer(Modifier.height(16.dp))
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.terms_title),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(8.dp))
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClickTermsOfService),
                headlineContent = { Text(text = stringResource(R.string.terms_of_service)) }
            )
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClickTermsOfPrivacy),
                headlineContent = { Text(text = stringResource(R.string.terms_of_privacy_info)) }
            )

            Spacer(Modifier.height(16.dp))
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "버전 정보",
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(8.dp))
            ListItem(
                modifier = Modifier.fillMaxWidth(),
                headlineContent = { Text(text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})") }
            )
        }

        if (showPasswordChangeDialog) {
            PasswordChangeDialog(
                onDismissRequest = { showPasswordChangeDialog = false },
                onConfirmClick = {
                    onChangePassword(it)
                    showPasswordChangeDialog = false
                }

            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(text = "로그아웃") },
                text = { Text(text = "로그아웃 하시겠습니까? 모든 데이터가 삭제됩니다.") },
                confirmButton = {
                    TextButton(onClick = {
                        onLogout()
                        showLogoutDialog = false
                    }) {
                        Text(text = "로그아웃")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text(text = "취소")
                    }
                },
            )
        }

        if (showCurrentSemesterSettingDialog && settingUiState is SettingUiState.UserEditableSettings) {
            CurrentSemesterSettingDialog(
                year = settingUiState.specifiedCurrentSemester?.first ?: 2026,
                semester = settingUiState.specifiedCurrentSemester?.second ?: SemesterType.One,
                onConfirmClick = { year, semester ->
                    onSpecifiedCurrentSemester(year, semester)
                    showCurrentSemesterSettingDialog = false
                },
                onDismissRequest = { showCurrentSemesterSettingDialog = false },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordChangeDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: (password: String) -> Unit,
) {
    var showPassword by remember { mutableStateOf(false) }
    val isLoading by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = { password = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onConfirmClick(password) }
                    ),
                    enabled = !isLoading,
                    label = { Text(text = "유세인트 비밀번호") },
                    singleLine = true,
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { showPassword = !showPassword },
                            enabled = !isLoading,
                        ) {
                            Icon(
                                if (showPassword) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                },
                                contentDescription = "Toggle password visibility",
                                modifier = Modifier
                                    .requiredSize(48.dp)
                                    .padding(16.dp)
                            )
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onConfirmClick(password) },
                    enabled = !isLoading,
                ) {
                    Text(text = "변경")
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.saint_login_announce),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrentSemesterSettingDialog(
    year: Int,
    semester: SemesterType,
    onConfirmClick: (year: Int, semester: SemesterType) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var yearText by rememberSaveable { mutableStateOf(year.toString()) }
    var selectedSemester by rememberSaveable { mutableStateOf(semester) }
    var confirmButtonEnabled by rememberSaveable { mutableStateOf(true) }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(Modifier.padding(24.dp)) {
                OutlinedTextField(
                    value = yearText,
                    onValueChange = {
                        yearText = it
                        confirmButtonEnabled = yearText.toIntOrNull() != null
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    label = { Text(text = "연도") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                SemesterType.entries.forEach { semester ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSemester = semester },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = semester == selectedSemester,
                            onClick = { selectedSemester = semester },
                        )
                        Text(text = "${semester.kor}학기")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("취소")
                    }
                    TextButton(
                        onClick = { onConfirmClick(yearText.toInt(), selectedSemester) },
                        enabled = confirmButtonEnabled,
                    ) {
                        Text("확인")
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun SettingScreenPreview() {
    var notiToggle by remember { mutableStateOf(false) }
    var currentSemesterToggle by remember { mutableStateOf(false) }
    var specifiedSemester: Pair<Int, SemesterType> by remember { mutableStateOf(2025 to SemesterType.One) }
    var autoFetchToggle by remember { mutableStateOf(false) }
    SoomsilUSaintTheme {
        SettingScreen(
            settingUiState = SettingUiState.UserEditableSettings(
                notiToggle,
                currentSemesterToggle,
                specifiedSemester,
                autoFetchToggle,
            ),
            onNotificationToggleChange = { notiToggle = it },
            onSpecifiedCurrentSemester = { year, semester ->
                currentSemesterToggle = true
                specifiedSemester = year to semester
            },
            onChangePassword = {},
            onUnspecifiedCurrentSemester = { currentSemesterToggle = false },
            onAutoFetchToggleChange = { autoFetchToggle = it },
        )
    }
}

@PreviewLightDark
@Composable
private fun DialogPreview() {
    SoomsilUSaintTheme {
        CurrentSemesterSettingDialog(
            year = 2025,
            semester = SemesterType.One,
            onConfirmClick = { _, _ -> },
            onDismissRequest = {},
        )
    }
}
