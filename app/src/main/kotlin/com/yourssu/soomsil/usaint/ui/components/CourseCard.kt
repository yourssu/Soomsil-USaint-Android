package com.yourssu.soomsil.usaint.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview

data class CourseCardColors(
    val background: Color,
    val text: Color
)

@Composable
@Preview
fun CourseCard(
    courseName: String = "객체지향 프로그래밍",
    professor: String = "최지웅",
    credit: String = "3",
    grade: String = "A+",
    modifier: Modifier = Modifier
) {
    val colors = gradeColors(grade)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F4F5), RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 배지 (성적 등급)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(colors.background, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = grade,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            }

            // 과목 정보
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = courseName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF18181B)
                )
                Text(
                    text = "$professor · ${credit}학점",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFB0B8C1)
                )
            }

            // 성적 태그
            Box(
                modifier = Modifier
                    .background(colors.background, RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = grade,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.text
                )
            }
        }
    }
}
fun gradeColors(grade: String): CourseCardColors {
    return when (grade) {
        "P", "Pass" -> CourseCardColors(
            background = Color(0xFFF0ECFF),
            text = Color(0xFF775EFF)
        )
        "A+" -> CourseCardColors(
            background = Color(0xFFECFDF5),
            text = Color(0xFF059669)
        )
        "A-", "A0" -> CourseCardColors(
            background = Color(0xFFEFF6FF),
            text = Color(0xFF2563EB)
        )
        else -> CourseCardColors(
            background = Color(0xFFF4F4F5),
            text = Color(0xFF8B95A1)
        )
    }
}
