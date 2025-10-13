package com.yourssu.soomsil.usaint.screen.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourssu.soomsil.usaint.core.extensions.detectPinchGestures
import com.yourssu.soomsil.usaint.screen.home.model.SeatAlignment
import com.yourssu.soomsil.usaint.screen.home.model.SeatData
import com.yourssu.soomsil.usaint.screen.home.model.SeatId
import com.yourssu.soomsil.usaint.screen.home.model.SeatInfo
import kotlin.math.max
import kotlin.math.min

enum class ZoomLevel { LEVEL2, LEVEL1, LEVEL0 }

@Composable
fun ChapelSeatLayout(
    seatNumber: String,
    modifier: Modifier = Modifier,
) {
    var zoomLevel by remember { mutableStateOf(ZoomLevel.LEVEL2) }
    val (sector, row, column) = seatNumber.split("-")
    val mySeat = SeatId(sector.trim(), row.trim().toInt(), column.trim().toInt())

    ChapelSeatRoot(
        mySeat = mySeat,
        zoomLevel = zoomLevel,
        onZoomChange = { zoomLevel = it },
        modifier = modifier,
    )
}

@Composable
fun ChapelSeatRoot(
    mySeat: SeatId,
    zoomLevel: ZoomLevel,
    onZoomChange: (ZoomLevel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var zoom by remember { mutableFloatStateOf(1f) }
    val zoomTransition by animateFloatAsState(
        zoom,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(zoomLevel) {
                detectPinchGestures(
                    pass = PointerEventPass.Initial,
                    onGesture = { _: Offset, newZoom: Float ->
                        val newScale = zoom * newZoom
                        if (newScale > 1.25f) {
                            when (zoomLevel) {
                                ZoomLevel.LEVEL2 -> {
                                    onZoomChange(ZoomLevel.LEVEL1)
                                    zoom = 1f
                                }

                                ZoomLevel.LEVEL1 -> {
                                    onZoomChange(ZoomLevel.LEVEL0)
                                    zoom = 1f
                                }

                                else -> zoom = newScale
                            }
                        } else if (newScale < 0.75f) {
                            when (zoomLevel) {
                                ZoomLevel.LEVEL1 -> {
                                    onZoomChange(ZoomLevel.LEVEL2)
                                    zoom = 1f
                                }

                                ZoomLevel.LEVEL0 -> {
                                    onZoomChange(ZoomLevel.LEVEL1)
                                    zoom = 1f
                                }

                                else -> zoom = newScale
                            }
                        } else {
                            zoom = newScale
                        }
                    },
                    onGestureEnd = { zoom = 1f }
                )
            }
            .graphicsLayer {
                scaleX = zoomTransition
                scaleY = zoomTransition
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        ChapelSeatLayout(
            mySeat = mySeat,
            zoomLevel = zoomLevel,
        )
    }
}

@Composable
fun ChapelSeatLayout(
    mySeat: SeatId,
    zoomLevel: ZoomLevel,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // LEVEL2: 전체 보기 (1층 + 2층)
        AnimatedVisibility(
            visible = zoomLevel == ZoomLevel.LEVEL2,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Level2View(mySeat)
        }

        // LEVEL1: 자신이 속한 층만 보기
        AnimatedVisibility(
            visible = zoomLevel == ZoomLevel.LEVEL1,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Level1View(mySeat)
        }

        // LEVEL0: 자신이 속한 섹션의 일부만 보기 (위/아래 구분)
        AnimatedVisibility(
            visible = zoomLevel == ZoomLevel.LEVEL0,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Level0View(mySeat)
        }
    }
}

@Composable
fun Level2View(
    mySeat: SeatId,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        val availableWidth = maxWidth - 32.dp

        // 각 층의 최대 좌석 수 계산
        val floor1MaxSeats = listOf("A", "B", "C", "D", "E").sumOf { sector ->
            SeatData.floor1[sector]?.seatList?.maxOfOrNull { it.size } ?: 0
        }
        val floor2MaxSeats = listOf("F", "G", "H", "I", "J").sumOf { sector ->
            SeatData.floor2[sector]?.seatList?.maxOfOrNull { it.size } ?: 0
        }
        val maxTotalSeatsWidth = maxOf(floor1MaxSeats, floor2MaxSeats)

        // 섹션 간격 고려
        val numSections = 5
        val sectionSpacing = 8.dp
        val totalSpacing = sectionSpacing * (numSections - 1)

        // 좌석 간격
        val seatSpacing = 1.dp
        val totalSeatSpacing = seatSpacing * 2 * maxTotalSeatsWidth

        // 좌석 크기 계산
        val calculatedSeatSize = (availableWidth - totalSpacing - totalSeatSpacing) / maxTotalSeatsWidth
        val seatSize = min(calculatedSeatSize.value, 16f).dp
        val fontSize = max((seatSize.value * 0.4f).sp.value, 6f).sp

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "STAGE",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))
            Floor1(mySeat, seatSize = seatSize, fontSize = fontSize, sectionSpacing = sectionSpacing, seatSpacing = seatSpacing)
            Spacer(Modifier.height(16.dp))
            Floor2(mySeat, seatSize = seatSize, fontSize = fontSize, sectionSpacing = sectionSpacing, seatSpacing = seatSpacing)
        }
    }
}

@Composable
fun Level1View(
    mySeat: SeatId,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        val availableWidth = maxWidth - 32.dp

        val sectors = if (mySeat.sector in listOf("A", "B", "C", "D", "E")) {
            listOf("A", "B", "C", "D", "E")
        } else {
            listOf("F", "G", "H", "I", "J")
        }

        val maxTotalSeatsWidth = sectors.sumOf { sector ->
            (if (mySeat.sector in listOf("A", "B", "C", "D", "E")) {
                SeatData.floor1[sector]
            } else {
                SeatData.floor2[sector]
            })?.seatList?.maxOfOrNull { it.size } ?: 0
        }

        val numSections = 5
        val sectionSpacing = 12.dp
        val totalSpacing = sectionSpacing * (numSections - 1)

        val seatSpacing = 1.dp
        val totalSeatSpacing = seatSpacing * 2 * maxTotalSeatsWidth

        val calculatedSeatSize = (availableWidth - totalSpacing - totalSeatSpacing) / maxTotalSeatsWidth
        val seatSize = min(calculatedSeatSize.value, 24f).dp
        val fontSize = max((seatSize.value * 0.4f).sp.value, 8f).sp

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "STAGE",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(vertical = 6.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))
            if (mySeat.sector in listOf("A", "B", "C", "D", "E")) {
                Floor1(mySeat, seatSize = seatSize, fontSize = fontSize, sectionSpacing = sectionSpacing, seatSpacing = seatSpacing)
            } else {
                Floor2(mySeat, seatSize = seatSize, fontSize = fontSize, sectionSpacing = sectionSpacing, seatSpacing = seatSpacing)
            }
        }
    }
}

@Composable
fun Level0View(
    mySeat: SeatId,
    modifier: Modifier = Modifier,
) {
    val mySeatInfo = SeatData.floor1[mySeat.sector] ?: SeatData.floor2[mySeat.sector] ?: return

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        val availableWidth = maxWidth - 32.dp

        val half = mySeatInfo.spaceIdx
        val isUpperHalf = mySeat.row <= half
        val filtered = if (isUpperHalf) {
            mySeatInfo.seatList.take(half)
        } else {
            mySeatInfo.seatList.drop(half)
        }

        val maxSeatsInView = filtered.maxOfOrNull { it.size } ?: 0
        val calculatedSeatSize = availableWidth / (maxSeatsInView + 2)
        val seatSize = min(calculatedSeatSize.value, 40f).dp
        val fontSize = (seatSize.value * 0.35f).sp

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "STAGE",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Spacer(Modifier.height(24.dp))
            SectorDetail(
                mySeat = mySeat,
                mySeatInfo = mySeatInfo,
                seatSize = seatSize,
                fontSize = fontSize
            )
        }
    }
}

@Composable
fun Floor1(
    mySeat: SeatId,
    modifier: Modifier = Modifier,
    seatSize: Dp = 28.dp,
    fontSize: TextUnit = 10.sp,
    sectionSpacing: Dp = 16.dp,
    seatSpacing: Dp = 1.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(sectionSpacing),
    ) {
        listOf("A", "B", "C", "D", "E").forEach { sector ->
            val seatInfo = SeatData.floor1[sector] ?: return@forEach
            ChapelSeatSection(
                sectionName = sector,
                seatList = seatInfo.seatList,
                mySeatId = mySeat.id,
                seatAlignment = seatInfo.frontAlignment,
                seatSize = seatSize,
                fontSize = fontSize,
                spaceIdx = seatInfo.spaceIdx,
                backAlignment = seatInfo.backAlignment,
                seatSpacing = seatSpacing,
                additionalIdx = seatInfo.additionalIdx,
            )
        }
    }
}

@Composable
fun Floor2(
    mySeat: SeatId,
    modifier: Modifier = Modifier,
    seatSize: Dp = 28.dp,
    fontSize: TextUnit = 10.sp,
    sectionSpacing: Dp = 16.dp,
    seatSpacing: Dp = 1.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(sectionSpacing)
    ) {
        listOf("F", "G", "H", "I", "J").forEach { sector ->
            val seatInfo = SeatData.floor2[sector] ?: return@forEach
            ChapelSeatSection(
                sectionName = sector,
                seatList = seatInfo.seatList,
                mySeatId = mySeat.id,
                seatAlignment = seatInfo.frontAlignment,
                seatSize = seatSize,
                fontSize = fontSize,
                spaceIdx = seatInfo.spaceIdx,
                backAlignment = seatInfo.backAlignment,
                seatSpacing = seatSpacing,
                additionalIdx = seatInfo.additionalIdx,
            )
        }
    }
}

@Composable
fun SectorDetail(
    mySeat: SeatId,
    mySeatInfo: SeatInfo,
    modifier: Modifier = Modifier,
    seatSize: Dp = 40.dp,
    fontSize: TextUnit = 14.sp
) {
    val seatList = mySeatInfo.seatList
    val half = mySeatInfo.spaceIdx

    val isUpperHalf = mySeat.row <= half
    val filtered = if (isUpperHalf) {
        seatList.take(half)
    } else {
        seatList.drop(half)
    }

    val alignment = if (isUpperHalf) {
        mySeatInfo.frontAlignment
    } else {
        mySeatInfo.backAlignment
    }

    ChapelSeatSection(
        sectionName = mySeat.sector,
        seatList = filtered,
        mySeatId = mySeat.id,
        seatAlignment = alignment,
        rowIndexOffset = if (isUpperHalf) 0 else half,
        seatSize = seatSize,
        fontSize = fontSize,
        spaceIdx = mySeatInfo.spaceIdx,
        backAlignment = mySeatInfo.backAlignment,
        additionalIdx = mySeatInfo.additionalIdx,
        seatSpacing = 4.dp,
        modifier = modifier,
    )
}

@Composable
fun ChapelSeatSection(
    sectionName: String,
    seatList: List<List<Int>>,
    mySeatId: String,
    seatAlignment: SeatAlignment,
    modifier: Modifier = Modifier,
    rowIndexOffset: Int = 0,
    seatSize: Dp = 28.dp,
    fontSize: TextUnit = 10.sp,
    spaceIdx: Int? = null,
    backAlignment: SeatAlignment? = null,
    seatSpacing: Dp = 1.dp,
    additionalIdx: Int? = null,
) {

    val maxSeatsInSection = seatList.maxOfOrNull { it.size } ?: 0
    val rowWidth = seatSize * maxSeatsInSection + seatSpacing * maxSeatsInSection * 2 + seatSpacing * 3


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = sectionName,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp),
            fontSize = fontSize * 1.2f
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            seatList.forEachIndexed { rowIndex, rowSeats ->
                val actualRowIndex = rowIndex + 1 + rowIndexOffset
                val isFront = spaceIdx == null || actualRowIndex <= spaceIdx
                val isAdditional = additionalIdx != null && actualRowIndex >= additionalIdx + 1
                val currentAlignment = if (!isFront && backAlignment != null) {
                    backAlignment
                } else {
                    seatAlignment
                }

                val additionalPadding = if(isAdditional) seatSize * 2 + seatSpacing * 4 else 0.dp

                // spaceIdx 직후에 간격 추가
                if (spaceIdx != null && actualRowIndex == spaceIdx + 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                } else if (rowIndex > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                val rowArrangement = when (currentAlignment) {
                    SeatAlignment.LEFT -> Arrangement.Start
                    SeatAlignment.CENTER -> Arrangement.Center
                    SeatAlignment.RIGHT -> Arrangement.End
                }

                Row(
                    horizontalArrangement = rowArrangement,
                    modifier = Modifier
                        .width(rowWidth)
                        .padding(
                            start = if (sectionName == "F") additionalPadding else 0.dp,
                            end = if (sectionName != "F") additionalPadding else 0.dp
                        )
                ) {
                    rowSeats.forEachIndexed { _, seatNumber ->
                        val id = "$sectionName-$actualRowIndex-$seatNumber"
                        ChapelSeatItem(
                            id = id,
                            isMine = id.equals(mySeatId, ignoreCase = true),
                            modifier = Modifier.padding(horizontal = seatSpacing),
                            seatSize = seatSize,
                            fontSize = fontSize
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapelSeatItem(
    id: String,
    isMine: Boolean,
    modifier: Modifier = Modifier,
    seatSize: Dp = 28.dp,
    fontSize: TextUnit = 10.sp,
) {
    val seatColor =
        if (isMine) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)

    val cornerRadius = seatSize * 0.15f

    Box(
        modifier = modifier
            .size(seatSize)
            .clip(RoundedCornerShape(cornerRadius))
            .background(seatColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = id.split("-").last(),
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}