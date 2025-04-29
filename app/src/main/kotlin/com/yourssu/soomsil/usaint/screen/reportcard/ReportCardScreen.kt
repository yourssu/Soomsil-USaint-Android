package com.yourssu.soomsil.usaint.screen.reportcard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun ReportCardScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportCardViewModel = hiltViewModel(),
) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportCardScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = "성적") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {

        }
    }
}

@PreviewLightDark
@Composable
private fun ReportCardScreenPreview() {
    SoomsilUSaintTheme {
        ReportCardScreen()
    }
}
