package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.entity.asEntity
import javax.inject.Inject
import kotlin.collections.map

class SemesterRepository @Inject constructor(
    private val semesterDao: SemesterDao
)  {
    suspend fun storeSemesters(vararg semesters: SemesterData): Result<Unit> {
        return runCatching {
            semesterDao.upsertSemesters(semesters.map(SemesterData::asEntity))
        }
    }
}