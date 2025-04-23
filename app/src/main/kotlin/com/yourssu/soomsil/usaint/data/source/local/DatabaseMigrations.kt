package com.yourssu.soomsil.usaint.data.source.local

import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.migration.AutoMigrationSpec

object DatabaseMigrations {
    @DeleteColumn(
        tableName = "Lecture",
        columnName = "id",
    )
    @DeleteColumn(
        tableName = "Lecture",
        columnName = "semesterId",
    )
    @DeleteColumn(
        tableName = "Semester",
        columnName = "totalReportCardId",
    )
    @DeleteColumn(
        tableName = "Semester",
        columnName = "id",
    )
    @DeleteTable(tableName = "total_report_card")
    class Schema3to4 : AutoMigrationSpec
}