package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.yourssu.soomsil.usaint.data.source.local.entity.ChapelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapelDao {
    @Query("SELECT * FROM Chapel WHERE year = :year AND semester = :semesterName")
    fun getChapelEntity(year: Int, semesterName: String): Flow<ChapelEntity>

    @Query("SELECT * FROM Chapel")
    fun getLectureEntities(): Flow<List<ChapelEntity>>

    @Upsert
    suspend fun upsertChapel(entity: ChapelEntity)

    @Query("DELETE FROM Chapel WHERE year = :year AND semester = :semesterName")
    suspend fun deleteChapelEntitiesWithYearSemester(year: Int, semesterName: String)
}