package com.yourssu.soomsil.usaint.screen.reportcard.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.yourssu.soomsil.usaint.R
import com.yourssu.soomsil.usaint.core.model.Fail
import com.yourssu.soomsil.usaint.core.model.LectureGrade
import com.yourssu.soomsil.usaint.core.model.Pass
import com.yourssu.soomsil.usaint.core.model.Unknown
import com.yourssu.soomsil.usaint.screen.semesterdetail.CaptureFlag
import com.yourssu.soomsil.usaint.ui.theme.SoomsilUSaintTheme

@Composable
fun LectureItem(
    lectureGrade: LectureGrade,
    lectureTitle: String,
    professor: String,
    credit: Float,
    modifier: Modifier = Modifier,
    captureFlag: CaptureFlag = CaptureFlag.None,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.padding(
                top = 16.dp,
                bottom = 16.dp,
                end = 16.dp,
            ),
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = lectureGrade.resourceId()),
                contentScale = ContentScale.Fit,
                contentDescription = "grade",
            )
        }
        if (captureFlag is CaptureFlag.HidingInfo) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(25.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            )
        } else {
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
        }
    }
}

@DrawableRes
private fun LectureGrade.resourceId(): Int = when (this) {
    is Pass -> R.drawable.ic_tier_pass
    is Fail -> R.drawable.ic_tier_fail
    is Unknown -> R.drawable.ic_tier_unknown
    is LectureGrade.Grade -> when (this.grade) {
        "A+" -> R.drawable.ic_tier_ap
        "A0" -> R.drawable.ic_tier_a0
        "A-" -> R.drawable.ic_tier_am
        "B+" -> R.drawable.ic_tier_bp
        "B0" -> R.drawable.ic_tier_b0
        "B-" -> R.drawable.ic_tier_bm
        "C+" -> R.drawable.ic_tier_cp
        "C0" -> R.drawable.ic_tier_c0
        "C-" -> R.drawable.ic_tier_cm
        "D+" -> R.drawable.ic_tier_dp
        "D0" -> R.drawable.ic_tier_d0
        "D-" -> R.drawable.ic_tier_dm
        "P" -> R.drawable.ic_tier_pass
        "F" -> R.drawable.ic_tier_fail
        else -> R.drawable.ic_tier_unknown
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
                    lectureGrade = lectureGrade,
                    lectureTitle = "가나다라",
                    professor = "홍길동",
                    credit = 3f,
                    captureFlag = captureFlag,
                )
            }
        }
    }
}
