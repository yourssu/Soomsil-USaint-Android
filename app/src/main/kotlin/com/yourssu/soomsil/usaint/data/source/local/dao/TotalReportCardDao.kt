package com.yourssu.soomsil.usaint.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCard
import com.yourssu.soomsil.usaint.data.source.local.entity.TotalReportCardWithSemesters

@Dao
interface TotalReportCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTotalReportCard(totalReportCard: TotalReportCard): Long

    @Transaction
    @Query("SELECT * FROM total_report_card WHERE id = :id")
    fun getTotalReportCardWithSemesters(id: Int): TotalReportCardWithSemesters?
}