package com.yourssu.soomsil.usaint.data.source.local

import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("PRAGMA foreign_keys=off")

            // ==================== Chapel 테이블 마이그레이션 시작 ====================

            // 1-1. Chapel: 새로운 스키마로 임시 테이블 생성
            db.execSQL("""
                CREATE TABLE Chapel_temp (
                    year INTEGER NOT NULL DEFAULT 0,
                    semester TEXT NOT NULL DEFAULT 'One',
                    division INTEGER NOT NULL DEFAULT 0,
                    chapelTime TEXT NOT NULL,
                    chapelRoom TEXT NOT NULL,
                    floorLevel INTEGER NOT NULL,
                    seatNumber TEXT NOT NULL,
                    absenceTime INTEGER NOT NULL,
                    result TEXT NOT NULL,
                    PRIMARY KEY(year, semester, division)
                )
            """)
            db.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS index_Chapel_division
                ON Chapel_temp (division)
            """)

            // 1-2. Chapel: 기존 테이블에서 데이터 복사 및 변환
            db.execSQL("""
                INSERT INTO Chapel_temp (
                    year, semester, division, chapelTime, chapelRoom,
                    floorLevel, seatNumber, absenceTime, result
                )
                SELECT
                    (division % 100000) / 10 AS year,
                    CASE (division % 100000) % 10
                        WHEN 0 THEN 'One'
                        WHEN 1 THEN 'Two'
                        WHEN 2 THEN 'Summer'
                        WHEN 3 THEN 'Winter'
                        ELSE 'One'
                    END AS semester,
                    division / 100000 AS division,
                    chapelTime, chapelRoom, floorLevel, seatNumber, absenceTime, result
                FROM Chapel
            """)

            // 1-3. Chapel: 기존 테이블 삭제
            db.execSQL("DROP TABLE Chapel")

            // 1-4. Chapel: 임시 테이블 이름을 원래 테이블 이름으로 변경
            db.execSQL("ALTER TABLE Chapel_temp RENAME TO Chapel")

            // ===================== Chapel 테이블 마이그레이션 종료 =====================


            // 1. 새로운 스키마로 임시 테이블 생성 (FOREIGN KEY 구문 추가)
            db.execSQL("""
                CREATE TABLE ChapelAttendance_temp (
                    year INTEGER NOT NULL DEFAULT 0,
                    semester TEXT NOT NULL DEFAULT 'One',
                    division INTEGER NOT NULL DEFAULT 0,
                    classDate TEXT NOT NULL,
                    category TEXT NOT NULL,
                    instructor TEXT NOT NULL,
                    instructorDepartment TEXT NOT NULL,
                    title TEXT NOT NULL,
                    attendance TEXT NOT NULL,
                    result TEXT NOT NULL,
                    note TEXT NOT NULL,
                    PRIMARY KEY(year, semester, classDate),
                    FOREIGN KEY(division) REFERENCES Chapel(division) ON UPDATE NO ACTION ON DELETE CASCADE
                )
            """)

            // UNIQUE 인덱스 생성 구문 추가
            db.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS index_ChapelAttendance_year_semester_classDate
                ON ChapelAttendance_temp (year, semester, classDate)
            """)


            // 2. 기존 테이블에서 데이터 복사 (이전과 동일)
            db.execSQL("""
                INSERT INTO ChapelAttendance_temp (
                    year, semester, division, classDate, category, instructor,
                    instructorDepartment, title, attendance, result, note
                )
                SELECT
                    (division % 100000) / 10 AS year,
                    CASE (division % 100000) % 10
                        WHEN 0 THEN 'One'
                        WHEN 1 THEN 'Two'
                        WHEN 2 THEN 'Summer'
                        WHEN 3 THEN 'Winter'
                        ELSE 'One'
                    END AS semester,
                    division / 100000 AS division,
                    classDate, category, instructor, instructorDepartment,
                    title, attendance, result, note
                FROM ChapelAttendance
            """)

            // 3. 기존 테이블 삭제
            db.execSQL("DROP TABLE ChapelAttendance")

            // 4. 임시 테이블 이름을 원래 테이블 이름으로 변경
            db.execSQL("ALTER TABLE ChapelAttendance_temp RENAME TO ChapelAttendance")

            db.execSQL("PRAGMA foreign_keys=on")
        }
    }

}