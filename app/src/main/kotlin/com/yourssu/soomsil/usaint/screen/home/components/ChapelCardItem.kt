package com.yourssu.soomsil.usaint.screen.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.core.model.ChapelAttendanceData
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.model.ChapelSimpleData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import kotlin.math.ceil

@Composable
fun ChapelCardItem(
    onChapelCardClick: () -> Unit,
    chapelData: ChapelData,
    totalAttendance: Int,
    currentAttendance: Int,
    modifier: Modifier = Modifier,
) {

    var seatExpanded by remember { mutableStateOf(false) }

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
                Row(
                    modifier = Modifier.clickable { seatExpanded = !seatExpanded }
                ) {
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
                            ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Icon(
                        imageVector = if (seatExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
                AnimatedVisibility(visible = seatExpanded) {
                    ChapelSeatLayout(
                        seatNumber = chapelData.chapelSimpleData.seatNumber,
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyChapelCardItem(
    onChapelCardClick: () -> Unit,
    currentSemester: SemesterData? = null,
    modifier: Modifier = Modifier,
) {

    var seatExpanded by remember { mutableStateOf(false) }

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

            if(currentSemester != null) {
                Text(
                    modifier = Modifier
                        .padding(
                            bottom = 4.dp,
                            start = 16.dp,
                            end = 16.dp,
                        ),
                    text = "${currentSemester.year}년 ${currentSemester.semester}학기의 채플 데이터를 받아오지 못했어요."
                )
            } else {
                Text(
                    modifier = Modifier
                        .padding(
                            bottom = 4.dp,
                            start = 16.dp,
                            end = 16.dp,
                        ),
                    text = "이번 학기의 채플 데이터를 받아오지 못했어요."
                )
            }
            HorizontalDivider(Modifier.padding(horizontal = 12.dp))
            Row(
                modifier = Modifier
                    .padding(
                        top = 4.dp,
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                        )
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "과거 채플 수강기록 확인하러 가기"
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

        }
    }
}

@Preview
@Composable
fun ChapelCardItemPreview() {
    SoomsilUSaintTheme {
        ChapelCardItem(
            chapelData = ChapelData(
                ChapelSimpleData.previewData,
                listOf(ChapelAttendanceData.previewData)
            ),
            onChapelCardClick = {},
            totalAttendance = 10,
            currentAttendance = 5,
        )
    }
}

@Preview
@Composable
fun EmptyChapelCardItemPreview() {
    SoomsilUSaintTheme {
        EmptyChapelCardItem(
            onChapelCardClick = {},
            currentSemester = null
        )
    }
}