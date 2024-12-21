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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.base.YdsText

@Composable
fun TwoButtonDialog(
    negativeButtonText: String,
    positiveButtonText: String,
    onNegativeButtonClicked: () -> Unit,
    onPositiveButtonClicked: () -> Unit,
    negativeButtonTextColor: Color = YdsTheme.colors.buttonWarned,
    positiveButtonTextColor: Color = YdsTheme.colors.logoDarkBlue,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
) {
    Dialog(onDismissRequest = onNegativeButtonClicked) {
        Card(
            modifier = modifier.padding(horizontal = 36.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (title != null) {
                    YdsText(
                        text = title,
                        style = YdsTheme.typography.subTitle2,
                        color = YdsTheme.colors.textPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }

                if (description != null) {
                    YdsText(
                        text = description,
                        style = YdsTheme.typography.body1,
                        color = YdsTheme.colors.textPrimary,
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
                    YdsText(
                        text = negativeButtonText,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { onNegativeButtonClicked() },
                        color = negativeButtonTextColor,
                        style = YdsTheme.typography.subTitle3
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    YdsText(
                        text = positiveButtonText,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { onPositiveButtonClicked() },
                        color = positiveButtonTextColor,
                        style = YdsTheme.typography.subTitle3
                    )

                }
            }
        }
    }
}

@Composable
@Preview
fun TwoButtonDialogPreview() {
    YdsTheme {
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
@Preview
fun TwoButtonDialogWithDescriptionPreview() {
    YdsTheme {
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