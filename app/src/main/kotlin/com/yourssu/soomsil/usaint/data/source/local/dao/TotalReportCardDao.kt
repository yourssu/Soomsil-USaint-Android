package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCard

@Dao
interface TotalReportCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTotalReportCard(totalReportCard: TotalReportCard)

    @Query("SELECT * FROM total_report_card LIMIT 1")
    suspend fun getTotalReportCard(): TotalReportCard?
}