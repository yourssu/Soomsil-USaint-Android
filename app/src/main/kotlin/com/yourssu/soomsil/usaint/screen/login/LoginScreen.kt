package com.yourssu.soomsil.usaint.screen.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.atom.BoxButton
import com.yourssu.design.system.compose.atom.PasswordTextField
import com.yourssu.design.system.compose.atom.SimpleTextField
import com.yourssu.design.system.compose.base.Icon
import com.yourssu.design.system.compose.base.IconSize
import com.yourssu.design.system.compose.base.YdsScaffold
import com.yourssu.design.system.compose.base.YdsText
import com.yourssu.design.system.compose.component.topbar.TopBar
import com.yourssu.soomsil.usaint.R
import com.yourssu.design.R as YdsR

@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    LoginScreen(
        studentId = viewModel.studentId,
        password = viewModel.studentPw,
        onStudentIdChange = { viewModel.studentId = it },
        onPasswordChange = { viewModel.studentPw = it },
        onLoginClick = {
            viewModel.setIdPw()
            println("저장!")
        },
        modifier = modifier,
    )
}

@Composable
fun LoginScreen(
    studentId: String,
    password: String,
    onStudentIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
//    onBackClick: () -> Unit = {},
) {
    YdsScaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                title = stringResource(R.string.login),
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            YdsText(
                text = stringResource(R.string.student_id),
                style = YdsTheme.typography.subTitle2,
                modifier = Modifier.padding(top = 24.dp, start = 20.dp),
            )

            SimpleTextField(
                text = studentId,
                modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                onValueChange = onStudentIdChange,
                onErrorChange = {},
            )

            YdsText(
                text = stringResource(R.string.password),
                style = YdsTheme.typography.subTitle2,
                modifier = Modifier.padding(top = 24.dp, start = 20.dp),
            )

            PasswordTextField(
                text = password,
                modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp),
                onValueChange = onPasswordChange,
                onErrorChange = {},
            )

            BoxButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 20.dp, end = 20.dp),
                text = stringResource(R.string.login),
                onClick = onLoginClick,
            )

            Row(
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 8.dp,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    id = YdsR.drawable.ic_warningcircle_line,
                    iconSize = IconSize.Small,
                    tint = YdsTheme.colors.textPointed,
                )
                Spacer(Modifier.width(8.dp))
                YdsText(
                    text = stringResource(id = R.string.saint_login_announce),
                    color = YdsTheme.colors.textPointed,
                    style = YdsTheme.typography.caption2,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoginScreenPreview() {
    var id by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    YdsTheme {
        LoginScreen(
            studentId = id,
            password = pw,
            onStudentIdChange = { id = it },
            onPasswordChange = { pw = it },
            onLoginClick = { },
        )
    }
}
