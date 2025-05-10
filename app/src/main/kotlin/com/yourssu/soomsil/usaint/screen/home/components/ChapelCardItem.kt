package com.yourssu.soomsil.usaint.screen.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.core.model.ChapelData

@Composable
fun ChapelCardItem(
    modifier: Modifier = Modifier,
    onChapelCardClick: () -> Unit,
    chapelData: ChapelData?
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onChapelCardClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 20.dp,
                ),
        ) {
            Text(
                text = "채플",
                modifier = Modifier
                    .padding(
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = buildAnnotatedString {
                    append("지금까지 ")
                    withStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.primary)
                    ) {
                        // TODO 'Pass까지 N회 남았어요'로 수정
                        // 주차별 채플 내용 불러올 수 있기 전까지 유지
                        append(
                            "${(chapelData?.absenceTime ?: 0)}회 "
                        )
                    }
                    append("결석했어요.")
                },
                modifier = Modifier
                    .padding(
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )

            LinearProgressIndicator(
                // TODO 출석완료 / 전체출석
                progress = { (14 - ((chapelData?.absenceTime)?.toFloat() ?: 0F)) / 14.toFloat() },
                modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
            )

            Row {
                Text(
                    // TODO 출석완료
                    text = "${14 - (chapelData?.absenceTime ?: 0)}회",
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            bottom = 4.dp,
                            start = 16.dp,
                            end = 16.dp,
                        ),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    // TODO 전체출석
                    text = "${14}회",
                    modifier = Modifier
                        .padding(
                            bottom = 4.dp,
                            start = 16.dp,
                            end = 16.dp,
                        ),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Column(
                modifier = Modifier
                    .padding(
                        top = 16.dp
                    )
            ) {
                Row {
                    Text(
                        text = "시간표",
                        modifier = Modifier
                            .padding(
                                bottom = 4.dp,
                                start = 16.dp,
                                end = 16.dp,
                            )
                            .weight(1f),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = chapelData?.chapelTime ?: "정보 없음",
                        modifier = Modifier
                            .padding(
                                bottom = 4.dp,
                                start = 16.dp,
                                end = 16.dp,
                            ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Row {
                    Text(
                        text = "좌석",
                        modifier = Modifier
                            .padding(
                                bottom = 4.dp,
                                start = 16.dp,
                                end = 16.dp,
                            )
                            .weight(1f),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = chapelData?.seatNumber ?: "정보 없음",
                        modifier = Modifier
                            .padding(
                                bottom = 4.dp,
                                start = 16.dp,
                                end = 16.dp,
                            ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }


        }
    }
}
