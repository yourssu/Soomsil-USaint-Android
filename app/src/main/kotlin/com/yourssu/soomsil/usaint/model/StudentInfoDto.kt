package com.yourssu.soomsil.usaint.model

import dev.eatsteak.rusaint.core.GradeSummary
import dev.eatsteak.rusaint.core.GraduationStudent

data class StudentInfoDto(
    /**
     * [dev.eatsteak.rusaint.core.GraduationStudent]
     */
    val id: String,                 // 학번
    val name: String,               // 이름
    val grade: UInt,                // 학년
    val semester: UInt,             // 이수학기 (ex: 7)
    val status: String,             // 학적 상태 (재학 or 휴학)
    val applyYear: UInt,            // 입학년도
    val applyType: String,          // 입학유형 (ex: 신입학)
    val department: String,         // 소속 (ex: IT대학)
    val majors: List<String>,       // 제1전공 ~ 제4전공
    val graduationPoints: Float,    // 졸업학점
    val completedPoints: Float,     // 인정학점

    /**
     * [dev.eatsteak.rusaint.core.GradeSummary]
     *
     * help me:
     * - 학적부와 증명의 차이점?
     * - 졸업사정표의 인정학점과 학적부 취득학점의 차이점?
     */
    val attemptedCredits: Float,    // 학적부 신청학점
    val earnedCredits: Float,       // 학적부 취득학점
    val gradePointsSum: Float,      // 학적부 평점계
    val gradePointsAverage: Float,  // 학적부 평점평균
    val arithmeticMean: Float,      // 학적부 산술평균
    val pfEarnedCredits: Float,     // 학적부 P/F학점
) {
    companion object {
        fun from(graduationStudent: GraduationStudent, gradeSummary: GradeSummary) = StudentInfoDto(
            id = graduationStudent.number.toString(),
            name = graduationStudent.name,
            grade = graduationStudent.grade,
            semester = graduationStudent.semester,
            status = graduationStudent.status,
            applyYear = graduationStudent.applyYear,
            applyType = graduationStudent.applyType,
            department = graduationStudent.department,
            majors = graduationStudent.majors,
            graduationPoints = graduationStudent.graduationPoints,
            completedPoints = graduationStudent.completedPoints,
            attemptedCredits = gradeSummary.attemptedCredits,
            earnedCredits = gradeSummary.earnedCredits,
            gradePointsSum = gradeSummary.gradePointsSum,
            gradePointsAverage = gradeSummary.gradePointsAvarage,
            arithmeticMean = gradeSummary.arithmeticMean,
            pfEarnedCredits = gradeSummary.pfEarnedCredits,
        )
    }
}
