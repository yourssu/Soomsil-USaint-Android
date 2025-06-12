package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.yourssu.soomsil.usaint.data.source.local.entity.LectureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LectureDao {

    // 아직 사용되지 않은 메서드
    @Query(
        """
        SELECT * FROM Lecture 
        WHERE year = :year AND semester = :semesterName AND code = :code
        """
    )
    fun getLectureEntity(year: Int, semesterName: String, code: String): Flow<LectureEntity>

    @Query("SELECT * FROM Lecture")
    fun getLectureEntities(): Flow<List<LectureEntity>>

    @Query("SELECT * FROM Lecture")
    suspend fun getOneOffLectureEntities(): List<LectureEntity>

    // 중복될 경우 update 하고, 그렇지 않다면 insert 합니다
    // 중복의 기준은 LectureEntity의 primary key입니다
    @Upsert
    suspend fun upsertLectures(entities: List<LectureEntity>)

    // insert 하지만 중복될 경우 반영되지 않습니다
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreLectures(entities: List<LectureEntity>): List<Long>

    // 연도와 학기에 해당하는 LectureEntity를 모두 삭제합니다
    @Query("DELETE FROM Lecture WHERE year = :year AND semester = :semesterName")
    suspend fun deleteLectureEntitiesWithYearSemester(year: Int, semesterName: String)

    @Query(
        """
    SELECT * FROM Lecture 
    WHERE year = :year AND semester = :semesterName
    """
    )
    suspend fun getLectureEntitiesForSemester(year: Int, semesterName: String): List<LectureEntity>
}