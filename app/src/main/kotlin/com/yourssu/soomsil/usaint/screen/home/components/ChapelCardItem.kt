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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.ui.entities.ChapelInfo
import kotlin.math.ceil

@Composable
fun ChapelCardItem(
    modifier: Modifier = Modifier,
    onChapelCardClick: () -> Unit,
    chapelInfo: ChapelInfo
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onChapelCardClick,
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 20.dp,
                ),
        ) {
            Text(
                text = stringResource(id = R.string.saint_chapel),
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

            Text(
                text = buildAnnotatedString {
                    append(
                        stringResource(
                            id = R.string.saint_chapel_pass_until
                        ).plus(" ")
                    )
                    withStyle(
                        SpanStyle(color = MaterialTheme.colorScheme.primary)
                    ) {
                        append(
                            stringResource(
                                id = R.string.saint_chapel_count,
                                ceil(chapelInfo.totalAttendance * (2F / 3F)).toInt() - chapelInfo.currentAttendance
                            ).plus(" ")
                        )
                    }
                    append(
                        stringResource(
                            id = R.string.saint_chapel_pass_left
                        )
                    )
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
                progress = { chapelInfo.currentAttendance.toFloat() / chapelInfo.totalAttendance.toFloat() },
                modifier.fillMaxWidth()
                    .padding(
                        bottom = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
            )

            Row {
                Text(
                    text = stringResource(id = R.string.saint_chapel_count, chapelInfo.currentAttendance),
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
                    text = stringResource(id = R.string.saint_chapel_count, chapelInfo.totalAttendance),
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
                        text = stringResource(id = R.string.saint_chapel_time),
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
                        text = chapelInfo.time,
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
                        text = stringResource(id = R.string.saint_chapel_seat_position),
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
                        text = chapelInfo.seat,
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
