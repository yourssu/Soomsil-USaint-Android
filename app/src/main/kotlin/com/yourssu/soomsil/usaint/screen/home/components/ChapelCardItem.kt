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
import kotlin.math.ceil

@Composable
fun ChapelCardItem(
    modifier: Modifier = Modifier,
    onChapelCardClick: () -> Unit,
    chapelData: ChapelData,
    totalAttendance: Int,
    currentAttendance: Int
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
                text =
                    buildAnnotatedString {
                        if(chapelData.chapelSimpleData.result == "P" || currentAttendance >= ceil(totalAttendance * (2 / 3F))) {
                            append("${chapelData.chapelSimpleData.year % 100}년도 ${chapelData.chapelSimpleData.semester.kor}학기 채플을 ")
                            withStyle(
                                SpanStyle(color = MaterialTheme.colorScheme.primary)
                            ) {
                                append(
                                    "Pass"
                                )
                            }
                            append("했어요!")
                        } else {
                            append("Pass까지 ")
                            withStyle(
                                SpanStyle(color = MaterialTheme.colorScheme.primary)
                            ) {
                                append(
                                    "${ceil(totalAttendance * (2 / 3.0)).toInt() - currentAttendance}회 "
                                )
                            }
                            append("남았어요.")
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

            LinearProgressIndicator(
                progress = { currentAttendance / totalAttendance.toFloat() },
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
                    text = "${currentAttendance}회",
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
                    text = "${totalAttendance}회",
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
                        text = chapelData.chapelSimpleData.chapelTime,
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
                        text = chapelData.chapelSimpleData.seatNumber,
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