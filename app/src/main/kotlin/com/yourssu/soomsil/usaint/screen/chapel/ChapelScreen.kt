package com.yourssu.soomsil.usaint.screen.chapel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChapelScreen(
    modifier: Modifier = Modifier,
    viewModel: ChapelViewModel = hiltViewModel(),
) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapelScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = "채플") })
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding),
        ) {

        }
    }
}