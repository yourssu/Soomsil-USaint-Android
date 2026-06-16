package com.yourssu.soomsil.usaint.screen.chapel.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.yourssu.soomsil.usaint.core.model.ChapelSimpleData
import kotlin.math.ceil

@Composable
fun ChapelSummary(
    modifier: Modifier = Modifier,
    chapelSimpleData: ChapelSimpleData,
    totalAttendance: Int,
    currentAttendance: Int,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 20.dp,
            ),
    ) {
        Text(
            text = "${chapelSimpleData.division.toUInt()} 분반",
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
                append("${chapelSimpleData.year}년도 ${chapelSimpleData.semester.kor}학기 채플은 ")
                withStyle(
                    SpanStyle(color = MaterialTheme.colorScheme.primary)
                ) {
                    append(
                        if (chapelSimpleData.result == "P")
                            "Pass!"
                        else {
                            if(chapelSimpleData.year >= 2026) {
                                //2026년부터 채플은 단 1회의 결석만 인정
                                if(totalAttendance - currentAttendance <= 1) {
                                    "Pass!"
                                } else {
                                    "Fail"
                                }
                            } else {
                                // 유세인트에 성적 결과은 없이 출결 상태만 있는 경우가 22년 2학기에 존재
                                if (currentAttendance >= ceil(totalAttendance * (2 / 3F)))
                                    "Pass!"
                                else
                                    "Fail"
                            }
                        }
                    )
                }
            },
            modifier = Modifier
                .padding(
                    bottom = 4.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

//        Text(
//            text = "${chapelSimpleData.absenceTime}회 결석 • ${currentAttendance}회 출석",
//            modifier = Modifier
//                .padding(
//                    bottom = 4.dp,
//                    start = 16.dp,
//                    end = 16.dp,
//                ),
//            style = MaterialTheme.typography.bodyMedium,
//            fontWeight = FontWeight.Normal,
//            color = MaterialTheme.colorScheme.onSurface,
//        )

        LinearProgressIndicator(
            progress = { currentAttendance.toFloat() / totalAttendance },
            modifier
                .fillMaxWidth()
                .padding(
                    bottom = 4.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
        )

//        Row {
//            Text(
//                text = "${currentAttendance}회",
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(
//                        bottom = 4.dp,
//                        start = 16.dp,
//                        end = 16.dp,
//                    ),
//                style = MaterialTheme.typography.titleSmall.copy(
//                    fontWeight = FontWeight.Bold
//                ),
//                color = MaterialTheme.colorScheme.primary,
//            )
//
//            Text(
//                text = "${totalAttendance}회",
//                modifier = Modifier
//                    .padding(
//                        bottom = 4.dp,
//                        start = 16.dp,
//                        end = 16.dp,
//                    ),
//                style = MaterialTheme.typography.titleSmall.copy(
//                    fontWeight = FontWeight.Normal
//                ),
//                color = MaterialTheme.colorScheme.onSurface,
//            )
//        }

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
                    text = chapelSimpleData.chapelTime,
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
                    text = chapelSimpleData.seatNumber,
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

@Composable
@Preview
fun PreviewChapelSummary() {
    ChapelSummary(chapelSimpleData = ChapelSimpleData.previewData, totalAttendance = 14, currentAttendance = 11)
}