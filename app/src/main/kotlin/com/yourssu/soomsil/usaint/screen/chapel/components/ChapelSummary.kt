package com.yourssu.soomsil.usaint.screen.chapel.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.core.model.ChapelData

@Composable
fun ChapelSummary(
    modifier: Modifier = Modifier,
    chapelData: ChapelData
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 20.dp,
                ),
        ) {
            Text(
                text = "${chapelData.division.toUInt()} 분반",
                modifier = Modifier
                    .padding(
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.tertiary,
            )

            Text(
                text = buildAnnotatedString {
                    append("${chapelData.year}년도 ${chapelData.semester.kor}학기 채플은 ")
                    withStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.primary)
                    ) {
                        // TODO 유세인트 채플 성적이 공란인 경우도 있음! 직접 계산해야함!
                        // 주차별 채플 내용 불러올 수 있기 전까지 유지
                        append(
                            if(chapelData.result == "P") "Pass!" else "Fail"
                        )
                    }
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

            Text(
                text = "${chapelData.absenceTime}회 결석 • ${0/** TODO 출석일수 구현 후 **/}회 출석",
                modifier = Modifier
                    .padding(
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
            )

            LinearProgressIndicator(
                // TODO 출석완료 / 전체출석
                progress = { (14 - chapelData.absenceTime.toFloat()) / 14.toFloat() },
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
                    text = "${14 - (chapelData.absenceTime)}회",
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
                        text = chapelData.chapelTime,
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
                        text = chapelData.seatNumber,
                        modifier = Modifier
                            .padding(
                                bottom = 4.dp,
                                start = 16.dp,
                                end = 16.dp,
                            ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }


        }
    }
}

@Composable
@Preview
fun previewChapleSummary() {
    ChapelSummary(chapelData = ChapelData.previewData)
}