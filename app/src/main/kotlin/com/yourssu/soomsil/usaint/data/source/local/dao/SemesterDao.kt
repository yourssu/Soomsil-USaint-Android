package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterWithLectures

@Dao
interface SemesterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: SemesterEntity): Long

    @Query("SELECT * FROM Semester WHERE year = :year AND semester = :semesterName LIMIT 1")
    suspend fun getSemesterByYearAndSemester(year: Int, semesterName: String): SemesterEntity?

    @Transaction
    @Query("SELECT * FROM Semester WHERE year = :year AND semester = :semesterName LIMIT 1")
    suspend fun getSemesterWithLectures(year: Int, semesterName: String): SemesterWithLectures?

    @Query("DELETE FROM Semester")
    suspend fun deleteAll()
}