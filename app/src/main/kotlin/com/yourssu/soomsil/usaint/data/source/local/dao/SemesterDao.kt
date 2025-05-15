package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.yourssu.soomsil.usaint.data.source.local.entity.ChapelEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.SemesterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SemesterDao {
    @Query("SELECT * FROM Semester WHERE year = :year AND semester = :semesterName")
    fun getSemesterEntity(year: Int, semesterName: String): Flow<SemesterEntity>

    @Query("SELECT * FROM Semester")
    fun getSemesterEntities(): Flow<List<SemesterEntity>>

    // 학기와 그에 대응되는 강의 정보 리스트를 쌍으로 반환합니다
    @Query(
        """
        SELECT * FROM Semester
        JOIN Lecture ON Semester.year = Lecture.year AND Semester.semester = Lecture.semester
        """
    )
    fun getSemesterWithLectures(): Flow<Map<SemesterEntity, List<LectureEntity>>>

    // 학기와 그에 대응되는 채플 정보 리스트를 쌍으로 반환합니다
    @Query(
        """
        SELECT * FROM Semester
        JOIN Chapel ON Semester.year = Chapel.year AND Semester.semester = Chapel.semester LIMIT 1
        """
    )
    fun getSemesterWithChapel(): Flow<Map<SemesterEntity, ChapelEntity>>

    @Query("SELECT * FROM Semester")
    suspend fun getOneOffSemesterEntities(): List<SemesterEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreSemesters(entities: List<SemesterEntity>): List<Long>

    @Upsert
    suspend fun upsertSemesters(entities: List<SemesterEntity>)

    @Query("DELETE FROM Semester")
    suspend fun deleteAllSemesters()
}