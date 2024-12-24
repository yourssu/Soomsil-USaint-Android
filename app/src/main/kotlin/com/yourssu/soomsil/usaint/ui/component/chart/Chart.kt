package com.yourssu.soomsil.usaint.ui.component.chart

import android.graphics.PointF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import com.yourssu.design.system.compose.YdsTheme
import com.yourssu.design.system.compose.rule.YdsBorder
import com.yourssu.soomsil.usaint.domain.type.makeSemesterType
import com.yourssu.soomsil.usaint.ui.entities.Grade
import com.yourssu.soomsil.usaint.ui.entities.Semester
import com.yourssu.soomsil.usaint.ui.entities.toGrade
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sqrt

// 참고한 코드
// https://github.com/riggaroo/compose-playtime/blob/main/app/src/main/java/dev/riggaroo/composeplaytime/SmoothLineGraph.kt
@Composable
fun Chart(
    chartData: ChartData,
    modifier: Modifier = Modifier,
    dotRadius: Dp = ChartDefaults.DotRadius,
    lineWidth: Dp = ChartDefaults.LineWidth,
    lineColor: Color = YdsTheme.colors.textPointed,
) {
    require(chartData.semesters.isNotEmpty()) {
        "chartData.semesters should not empty"
    }

    val dividerColor = YdsTheme.colors.borderNormal
    val fillBrush: Brush = SolidColor(lineColor.copy(alpha = 0.4f))

    val textMeasurer = rememberTextMeasurer()
    val axisTextStyle = YdsTheme.typography.subTitle3.toTextStyle()
    val axisTextColor = YdsTheme.colors.textTertiary

    val highlightTextStyle = YdsTheme.typography.caption0.toTextStyle().copy(
        fontWeight = FontWeight(600),
    )
    val highlightTextColor = YdsTheme.colors.textPointed
    val highlightBoxColor = YdsTheme.colors.buttonDisabledBG

    val grades: List<Grade> = chartData.semesters.map { it.gpa }
    val yAxis: List<Grade> = ChartDefaults.generateYAxisLabel(grades)

    val animationProgress = remember { Animatable(0f) }

    var highlightedIndex by remember { mutableStateOf<Int?>(null) }
    var graphOffsetX: Float? = null

    LaunchedEffect(chartData, grades) {
        // 최댓값의 인덱스로 초기화
        highlightedIndex = grades.indices.maxBy { grades[it] }
        animationProgress.animateTo(
            targetValue = 1f,
            tween(
                durationMillis = 2000,
                delayMillis = 300,
                easing = ChartDefaults.Easing,
            ),
        )
    }

    Spacer(
        modifier = modifier
            .padding(
                end = dotRadius,
                top = 40.dp,
            )
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val graphOffset = graphOffsetX ?: 0f
                        val selected = if (offset.x < graphOffset) {
                            0
                        } else {
                            val inputX = offset.x - graphOffset
                            val graphWidth = size.width - graphOffset
                            (inputX / (graphWidth / (chartData.semesters.size - 1)))
                                .roundToInt()
                                .coerceIn(0, chartData.semesters.size - 1)
                        }
                        highlightedIndex =
                            if (highlightedIndex == selected) null else selected
                    },
                )
            }
            .drawWithCache {
                val yAxisTextLayoutResults = yAxis.map { grade ->
                    textMeasurer.measure(
                        text = grade.formatToString(digit = 1),
                        style = axisTextStyle,
                    )
                }
                val xAxisTextLayoutResults = chartData.semesters.map { semester ->
                    textMeasurer.measure(
                        text = semester.type.shortHandedName,
                        style = axisTextStyle,
                    )
                }


                // y축 텍스트를 그리기 위한 공간
                val maxYAxisTextWidth = yAxisTextLayoutResults.maxBy { it.size.width }.size.width
                val graphStartPaddingPx = maxYAxisTextWidth +
                        ChartDefaults.VerticalGapBetweenTextAndAxis.toPx() +
                        dotRadius.toPx() +
                        xAxisTextLayoutResults.first().size.width / 2


                // x축 텍스트를 그리기 위한 공간
                val maxXAxisTextWidth = xAxisTextLayoutResults.maxBy { it.size.width }.size.width
                val maxXAxisTextHeight = xAxisTextLayoutResults.maxBy { it.size.height }.size.height
                val graphEndPaddingPx = xAxisTextLayoutResults.last().size.width / 2
                val xAxisTextRotateFlag = xAxisTextLayoutResults
                    .mapIndexed { i, textLayout ->
                        // 각 텍스트를 그리기 위한 최소한의 간격을 구함
                        if (i > 0) {
                            val before = xAxisTextLayoutResults[i - 1]
                            (before.size.width + textLayout.size.width) / 2f + ChartDefaults.HorizontalGapBetweenTexts.toPx()
                        } else {
                            0f
                        }
                    }
                    .any {
                        // 텍스트를 그리기 위한 최소한의 간격보다 각 포인트의 간격이 더 짧다면 flag = true
                        it > (size.width - graphStartPaddingPx - graphEndPaddingPx) / chartData.semesters.size
                    }
                val graphBottomPaddingPx = if (xAxisTextRotateFlag) {
                    (maxXAxisTextWidth + maxXAxisTextHeight) / sqrt(2f)
                } else {
                    maxXAxisTextHeight.toFloat()
                } + ChartDefaults.VerticalGapBetweenTextAndAxis.toPx()

                /** 차트(꺾은선 그래프) 변수 시작 */
                val graphSize = Size(
                    width = size.width - graphStartPaddingPx - graphEndPaddingPx,
                    height = size.height - graphBottomPaddingPx,
                )
                // 그래프 외곽 사각형의 lefttop, rightbottom
                val graphLeftTop = Offset(graphStartPaddingPx, lineWidth.toPx())
                val graphRightBottom = graphLeftTop + Offset(graphSize.width, graphSize.height)
                graphOffsetX = graphLeftTop.x

                val points = generatePoints(
                    grades = grades,
                    lowerBoundGrade = yAxis.last(),
                    size = graphSize,
                    graphLeftTop = graphLeftTop,
                )
                val path = generatePath(points)
                val filledPath = Path().apply {
                    addPath(path)
                    lineTo(graphRightBottom.x, graphRightBottom.y)
                    lineTo(graphLeftTop.x, graphRightBottom.y)
                    close()
                }

                onDrawBehind {
                    // draw Divider and yAxis mark(눈금)
                    val maximum = grades.max().value
                    val lowerGuideline = yAxis.last().value
                    val range = maximum - lowerGuideline
                    val horizontalInterval = graphSize.height / range // 1.0 사이의 간격

                    val barCount = yAxis.size

                    repeat(barCount) { i ->
                        val y = horizontalInterval * (maximum - yAxis[i].value) + graphLeftTop.y
                        val textLayout = yAxisTextLayoutResults[i]
                        drawText(
                            textLayout,
                            color = axisTextColor,
                            topLeft = Offset(0f, y - textLayout.size.height / 2),
                        )
                        drawLine(
                            color = dividerColor,
                            start = Offset(graphLeftTop.x, y),
                            end = Offset(graphSize.width + graphLeftTop.x, y),
                            strokeWidth = YdsBorder.Thin.dp.toPx(),
                        )
                    }

                    // draw graph path
                    clipRect(right = graphLeftTop.x + graphSize.width * animationProgress.value) {
                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(lineWidth.toPx()),
                        )
                        if (grades.size >= 2) {
                            drawPath(
                                path = filledPath,
                                brush = fillBrush,
                                style = Fill,
                            )
                        }
                    }

                    // draw dots and xAxis mark(눈금)
                    points.forEachIndexed { i, point ->
                        val textLayout = xAxisTextLayoutResults[i]
                        val textY =
                            graphSize.height + graphLeftTop.y + ChartDefaults.VerticalGapBetweenTextAndAxis.toPx()
                        rotate(
                            degrees = if (xAxisTextRotateFlag) -45f else 0f,
                            pivot = Offset(
                                x = point.x,
                                y = textY,
                            ),
                        ) {
                            drawText(
                                textLayout,
                                color = axisTextColor,
                                topLeft = Offset(
                                    x = if (xAxisTextRotateFlag) {
                                        point.x - textLayout.size.width
                                    } else {
                                        point.x - textLayout.size.width / 2
                                    },
                                    y = textY,
                                ),
                            )
                        }
                        drawCircle(
                            color = lineColor,
                            radius = dotRadius.toPx(),
                            center = Offset(point.x, point.y),
                        )
                    }

                    highlightedIndex?.let { idx ->
                        this.drawHighlight(
                            index = idx,
                            grades = grades,
                            points = points,
                            textMeasurer = textMeasurer,
                            textStyle = highlightTextStyle,
                            textColor = highlightTextColor,
                            containerColor = highlightBoxColor,
                        )
                    }
                }
            },
    )
}

private fun generatePoints(
    grades: List<Grade>,
    lowerBoundGrade: Grade,
    size: Size,
    graphLeftTop: Offset,
): List<PointF> {
    val maxGrade = grades.max().value
    val minGrade = lowerBoundGrade.value
    val range = maxGrade - minGrade
    val semesterWidth = if (grades.size > 1) size.width / (grades.size - 1) else 0f
    val heightPxPerAmount = size.height / range

    return grades.mapIndexed { i, grade ->
        val x = semesterWidth * i
        val y = size.height - (grade.value - minGrade) * heightPxPerAmount
        PointF(x + graphLeftTop.x, y + graphLeftTop.y)
    }
}

private fun generatePath(points: List<PointF>): Path {
    val path = Path()

    var previousX = points.first().x
    var previousY = points.first().y
    points.forEachIndexed { i, point ->
        val (x, y) = point
        if (i == 0) {
            path.moveTo(x, y)
        }
        // to do smooth curve graph - we use cubicTo, uncomment section below for non-curve
        val controlPoint1 = PointF((x + previousX) / 2f, previousY)
        val controlPoint2 = PointF((x + previousX) / 2f, y)
        path.cubicTo(
            controlPoint1.x,
            controlPoint1.y,
            controlPoint2.x,
            controlPoint2.y,
            x,
            y,
        )

        previousX = x
        previousY = y
    }
    return path
}

fun DrawScope.drawHighlight(
    index: Int,
    grades: List<Grade>,
    points: List<PointF>,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    textColor: Color,
    containerColor: Color,
) {
    if (index !in points.indices) return
    val (pointX, pointY) = points[index]
    val gradeText = grades[index].formatToString(digit = 2)
    val textLayoutResult = textMeasurer.measure(gradeText, style = textStyle)
    val containerSize = textLayoutResult.size.toSize().let { textSize ->
        textSize.copy(
            width = textSize.width + ChartDefaults.Highlight.ContainerHorizontalPadding.toPx() * 2,
            height = textSize.height + ChartDefaults.Highlight.ContainerVerticalPadding.toPx() * 2,
        )
    }
    val boxTopLeft = Offset(
        x = pointX - containerSize.width / 2,
        y = pointY - (containerSize.height + 10.dp.toPx()),
    )

    drawRoundRect(
        color = containerColor,
        topLeft = boxTopLeft,
        size = containerSize,
        cornerRadius = CornerRadius(ChartDefaults.Highlight.ContainerRadius.toPx()),
    )
    drawText(
        textLayoutResult,
        color = textColor,
        topLeft = boxTopLeft + Offset(
            ChartDefaults.Highlight.ContainerHorizontalPadding.toPx(),
            ChartDefaults.Highlight.ContainerVerticalPadding.toPx(),
        ),
    )
}

object ChartDefaults {
    val DotRadius = 4.dp
    val LineWidth = 3.dp
    val VerticalGapBetweenTextAndAxis = 4.dp
    val HorizontalGapBetweenTexts = 4.dp
    val Easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)

    object Highlight {
        val ContainerHorizontalPadding = 10.dp
        val ContainerVerticalPadding = 6.dp
        val ContainerRadius = 8.dp
    }

    private val LowerBoundGrade = Grade(2.0f)

    fun generateYAxisLabel(grades: List<Grade>): List<Grade> {
        // minimum이 하한(lower bound)보다 크면 하한으로 설정
        val minGrade = grades.min().coerceAtMost(LowerBoundGrade).value
        val maxGrade = grades.max().value
        val minLabel = floor(minGrade).roundToInt()
        val maxLabel = floor(maxGrade).roundToInt()

        return (minLabel..maxLabel).map {
            it.toFloat().toGrade()
        }.reversed()
    }
}

@Preview
@Composable
private fun ChartPreview() {
    YdsTheme {
        Box(
            modifier = Modifier
                .size(width = 300.dp, height = 200.dp)
                .background(YdsTheme.colors.bgNormal),
        ) {
            Chart(
                chartData = ChartData(
                    listOf(
                        Semester(makeSemesterType(2022, "1"), 3.5.toGrade()),
                        Semester(makeSemesterType(2022, "2"), 3.7.toGrade()),
                        Semester(makeSemesterType(2023, "1"), 4.2.toGrade()),
                        Semester(makeSemesterType(2023, "여름"), 4.5.toGrade()),
                    ),
                ),
            )
        }
    }
}

@Preview("single data")
@Composable
private fun ChartPreview_2() {
    YdsTheme {
        Box(
            modifier = Modifier
                .size(width = 300.dp, height = 200.dp)
                .background(YdsTheme.colors.bgNormal),
        ) {
            Chart(
                chartData = ChartData(
                    listOf(Semester(makeSemesterType(2022, "1"), 3.5.toGrade())),
                ),
            )
        }
    }
}

@Preview("many items")
@Composable
private fun ChartPreview_3() {
    YdsTheme {
        Box(
            modifier = Modifier
                .size(width = 300.dp, height = 200.dp)
                .background(YdsTheme.colors.bgNormal),
        ) {
            Chart(
                chartData = ChartData(
                    listOf(
                        Semester(makeSemesterType(2022, "1"), 3.5.toGrade()),
                        Semester(makeSemesterType(2022, "2"), 3.7.toGrade()),
                        Semester(makeSemesterType(2023, "1"), 4.2.toGrade()),
                        Semester(makeSemesterType(2023, "여름"), 4.5.toGrade()),
                        Semester(makeSemesterType(2023, "2"), 4.5.toGrade()),
                        Semester(makeSemesterType(2023, "겨울"), 4.5.toGrade()),
                        Semester(makeSemesterType(2024, "1"), 3.5.toGrade()),
                        Semester(makeSemesterType(2024, "겨울"), 1.5.toGrade()),
                    ),
                ),
            )
        }
    }
}
