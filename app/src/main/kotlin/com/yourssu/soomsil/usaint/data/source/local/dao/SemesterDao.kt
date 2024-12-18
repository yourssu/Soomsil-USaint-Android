package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterVO
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterWithLectures

@Dao
interface SemesterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSemester(semester: SemesterVO): Long

    @Query("SELECT * FROM Semester WHERE year = :year AND semester = :semesterName LIMIT 1")
    fun getSemesterByYearAndSemester(year: Int, semesterName: String): SemesterVO?

    @Query("SELECT * FROM Semester WHERE totalReportCardId = :totalReportCardId")
    fun getSemestersByTotalReportCardId(totalReportCardId: Int): List<SemesterVO>

    @Transaction
    @Query("SELECT * FROM Semester WHERE year = :year AND semester = :semesterName LIMIT 1")
    fun getSemesterWithLectures(year: Int, semesterName: String): SemesterWithLectures?
}