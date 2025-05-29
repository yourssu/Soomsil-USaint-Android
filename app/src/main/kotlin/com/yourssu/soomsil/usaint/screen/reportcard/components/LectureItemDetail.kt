package com.yourssu.soomsil.usaint.screen.reportcard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LectureItemDetail(
    detail: Map<String, String>
) {
    Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {

        detail.forEach { (key, value) ->
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Max)
            ) {
                Column(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        .padding(4.dp)
                        .fillMaxHeight()
                        .weight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = key,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        .padding(4.dp)
                        .fillMaxHeight()
                        .weight(0.2f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}