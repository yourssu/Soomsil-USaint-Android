package com.yourssu.soomsil.usaint.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun TwoButtonDialog(
    negativeButtonText: String,
    positiveButtonText: String,
    onNegativeButtonClicked: () -> Unit,
    onPositiveButtonClicked: () -> Unit,
    negativeButtonTextColor: Color = MaterialTheme.colorScheme.error,
    positiveButtonTextColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
) {
    Dialog(onDismissRequest = onNegativeButtonClicked) {
        Card(
            modifier = modifier.padding(horizontal = 36.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceBright
            ),
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                title?.let {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }

                description?.let {
                    Text(
                        text = description,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .align(Alignment.CenterHorizontally),
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = negativeButtonText,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable(onClick = onNegativeButtonClicked),
                        color = negativeButtonTextColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = positiveButtonText,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable(onClick = onPositiveButtonClicked),
                        color = positiveButtonTextColor,
                        fontWeight = FontWeight.SemiBold,
                    )

                }
            }
        }
    }
}

@Composable
@PreviewLightDark
fun TwoButtonDialogPreview() {
    SoomsilUSaintTheme {
        TwoButtonDialog(
            negativeButtonText = "취소",
            positiveButtonText = "확인",
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = {},
            title = "로그아웃 하시겠습니까?",
        )
    }
}

@Composable
@PreviewLightDark
fun TwoButtonDialogWithDescriptionPreview() {
    SoomsilUSaintTheme {
        TwoButtonDialog(
            negativeButtonText = "취소",
            positiveButtonText = "확인",
            onNegativeButtonClicked = {},
            onPositiveButtonClicked = {},
            title = "로그아웃 하시겠습니까?",
            description = "설명설명설명설명설명설명설명",
        )
    }
}