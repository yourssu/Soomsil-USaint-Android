package com.yourssu.soomsil.usaint.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.yourssu.soomsil.usaint.screen.setting.PasswordChangeDialog
import kotlinx.coroutines.launch

@Composable
fun PasswordSnackbarHandler(
    showSnackbar: Boolean,
    onDismiss: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    if (showSnackbar) {
        LaunchedEffect(Unit) {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = "유세인트 로그인에 실패했습니다.",
                actionLabel = "비밀번호 변경",
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> onPasswordChangeClick()
                SnackbarResult.Dismissed -> onDismiss()
            }
        }
    }
}

@Composable
fun PasswordDialogHandler(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmClick: (password: String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    if(showDialog) {
        PasswordChangeDialog(
            onDismissRequest = onDismiss,
            onConfirmClick = {
                onConfirmClick
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "앞으로 해당 비밀번호를 사용할게요. 정보를 다시 불러옵니다.",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    }
}