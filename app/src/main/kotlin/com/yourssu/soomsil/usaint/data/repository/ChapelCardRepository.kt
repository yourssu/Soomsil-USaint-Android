package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.source.local.dao.ChapelDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.datastore.ChapelDataSource
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.data.source.local.entity.ChapelEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.asEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.asExternalModel
import com.yourssu.soomsil.usaint.data.source.remote.USaintRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChapelCardRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val chapelDataSource: ChapelDataSource,
    private val semesterDao: SemesterDao,
    private val chapelDao: ChapelDao,
    private val uSaintRemoteSource: USaintRemoteSource,
) {
    val chapelCardData: Flow<ChapelData> =
        chapelDataSource.chapelCardData

    val semesterWithChapels: Flow<Map<SemesterData, List<ChapelData>>> =
        semesterDao.getSemesterWithChapels().map { semesterEntityListMap ->
            semesterEntityListMap
                .mapKeys { (semesterEntity, _) -> semesterEntity.asExternalModel() }
                .mapValues { (_, chapelEntity) -> chapelEntity.map(ChapelEntity::asExternalModel) }
        }

    suspend fun fetchChapelCardData(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        // TODO 현재 연도하고 학기를 뭘로 가져와야 하는거지???
        val chapelCardData = uSaintRemoteSource.remoteChapelData(credential, 2025, SemesterType.One)
        chapelDataSource.setChapelCardData(chapelCardData)
    }

    suspend fun fetchChapelData(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val semesterDataList = uSaintRemoteSource.remoteSemesterDataList(credential)
        semesterDao.upsertSemesters(semesterDataList.map(SemesterData::asEntity))

        for (semesterData in semesterDataList) {
            val chapelData = uSaintRemoteSource.remoteChapelData(
                credential,
                semesterData.year,
                semesterData.semester
            )
            chapelDao.upsertChapel(chapelData.asEntity())
        }
    }
}