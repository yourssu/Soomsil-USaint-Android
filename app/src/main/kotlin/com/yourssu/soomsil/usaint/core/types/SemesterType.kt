package com.yourssu.soomsil.usaint.core.types

/**
 * 학기의 종류를 구분하는 열거형입니다.
 *
 * @property kor 학기의 한글 표현입니다. '24-여름', '23-1'과 같이 표현할 때만 사용됩니다.
 *
 * @suppress 주의:
 * `LectureEntity.semester`과 `SemesterEntity.semester` 문자열을 지정할 땐 `kor` 대신 [Enum.name]한 뒤
 * [enumValueOf]으로 변환하여 사용해주세요.
 *
 * @sample com.yourssu.soomsil.usaint.data.source.local.entity.asExternalModel
 * @sample com.yourssu.soomsil.usaint.data.source.local.entity.asEntity
 */
enum class SemesterType(val kor: String) {
    One("1"),
    Summer("여름"),
    Two("2"),
    Winter("겨울");
}
