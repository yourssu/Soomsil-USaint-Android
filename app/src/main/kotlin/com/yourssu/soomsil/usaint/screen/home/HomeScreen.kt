package com.yourssu.soomsil.usaint.screen.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.screen.UiEvent
import com.yourssu.soomsil.usaint.ui.entities.ReportCardSummary
import com.yourssu.soomsil.usaint.ui.entities.StudentInfo
import com.yourssu.soomsil.usaint.ui.entities.toCredit
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import timber.log.Timber

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onReportCardClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { uiEvent ->
                when (uiEvent) {
                    is UiEvent.Failure -> {
                        Toast.makeText(
                            context,
                            uiEvent.msg ?: context.resources.getString(R.string.error_unknown),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is UiEvent.SessionFailure -> {
                        Toast.makeText(
                            context,
                            R.string.error_session_failure,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is UiEvent.RefreshFailure -> {
                        Toast.makeText(
                            context,
                            R.string.error_refresh_failure,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Timber.d("HomeScreen DisposableEffect ::: cancelJob")
            viewModel.cancelJob()
        }
    }

    HomeScreen(
        isRefreshing = viewModel.isRefreshing,
        onRefresh = viewModel::refresh,
        studentInfo = viewModel.studentInfo,
        reportCardSummary = viewModel.reportCardSummary,
        onProfileClick = onProfileClick,
        onSettingClick = onSettingClick,
        onReportCardClick = onReportCardClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    studentInfo: StudentInfo?,
    reportCardSummary: ReportCardSummary,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onReportCardClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp,
                    ),
            ) {
                StudentInfoItem(
                    studentInfo = studentInfo,
                    onProfileClick = onProfileClick,
                    onSettingClick = {
                        onSettingClick()
                    },
                )
                Spacer(Modifier.height(12.dp))
                ReportCardItem(
                    reportCardSummary = reportCardSummary,
                    onReportCardClick = onReportCardClick,
                )
            }
        }
    }
}

@Composable
fun ActionTitle(
    title: String,
    subTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp,
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            painter = painterResource(R.drawable.ic_default_profile_image),
            contentDescription = null,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = subTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}


@PreviewLightDark
@Composable
private fun HomePreview() {
    SoomsilUSaintTheme {
        HomeScreen(
            isRefreshing = false,
            onRefresh = {},
            studentInfo = StudentInfo(
                name = "홍길동",
                department = "컴퓨터학부",
                grade = 2,
            ),
            reportCardSummary = ReportCardSummary(
                gpa = 4.22.toGrade(),
                earnedCredit = 97.toCredit(),
                graduateCredit = 133.toCredit(),
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun ActionTitlePreview() {
    SoomsilUSaintTheme {
        ActionTitle(
            title = "asdf",
            subTitle = "asdfasdf",
            onClick = {},
        )
    }
}
