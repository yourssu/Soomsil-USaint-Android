package com.yourssu.soomsil.usaint.screen.reportcard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.core.model.LectureGrade
import com.yourssu.soomsil.usaint.screen.semesterdetail.CaptureFlag
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme
import com.yourssu.soomsil.usaint.ui.types.toCredit

@Composable
fun LectureItem(
    lectureGrade: LectureGrade,
    lectureTitle: String,
    professor: String,
    credit: Float,
//    captureFlag: CaptureFlag,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.padding(
                top = 16.dp,
                bottom = 16.dp,
                end = 16.dp,
            ),
        ) {
//            Image(
//                modifier = Modifier.size(48.dp),
//                painter = painterResource(id = tier.id),
//                contentScale = ContentScale.Fit,
//                contentDescription = "tier",
//            )
        }
//        if (captureFlag is CaptureFlag.HidingInfo) {
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .height(25.dp)
//                    .background(MaterialTheme.colorScheme.surfaceContainer),
//            )
//        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp),
            ) {
                Text(
                    text = lectureTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "$professor · ${String.format("%.1f", credit)}학점",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
//        }
    }
}

private class CaptureFlagParamProvider : PreviewParameterProvider<CaptureFlag> {
    override val values: Sequence<CaptureFlag>
        get() = sequenceOf(CaptureFlag.Original, CaptureFlag.HidingInfo)
}

@PreviewLightDark
@Composable
private fun CourseGradeItemPreview(
    @PreviewParameter(CaptureFlagParamProvider::class) captureFlag: CaptureFlag,
) {
    val lectureGrades = listOf(
        "A+", "A0", "A-",
        "B+", "B0", "B-",
        "C+", "C0", "C-",
        "D+", "D0", "D-",
        "P", "F", "Unknown"
    ).map { LectureGrade.from(it) }

    SoomsilUSaintTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            lectureGrades.forEach { lectureGrade ->
                LectureItem(
//                    tier = tier,
                    lectureGrade = lectureGrade,
                    lectureTitle = "가나다라",
                    professor = "홍길동",
                    credit = 3f,
//                    captureFlag = captureFlag,
                )
            }
        }
    }
}
