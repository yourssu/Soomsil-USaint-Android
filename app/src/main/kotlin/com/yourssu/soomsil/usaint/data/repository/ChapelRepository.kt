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

class ChapelRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val chapelDataSource: ChapelDataSource,
    private val semesterDao: SemesterDao,
    private val chapelDao: ChapelDao,
    private val uSaintRemoteSource: USaintRemoteSource,
) {
    val chapelCardData: Flow<ChapelData> =
        chapelDataSource.chapelCardData

    val semesterWithChapels: Flow<Map<SemesterData, ChapelData>> =
        semesterDao.getSemesterWithChapel().map { semesterEntityListMap ->
            semesterEntityListMap
                .mapKeys { (semesterEntity, _) -> semesterEntity.asExternalModel() }
                .mapValues { (_, chapelEntity) -> chapelEntity.asExternalModel() }
        }

    suspend fun fetchChapelCardData(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        // TODO 현재 연도하고 학기를 뭘로 가져와야 하는거지???
        val chapelCardData = uSaintRemoteSource.remoteChapelData(credential, 2025, SemesterType.One)
        chapelDataSource.setChapelCardData(chapelCardData)
    }

    suspend fun fetchSemesterWithChapels(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val semesterDataList = uSaintRemoteSource.remoteSemesterDataList(credential)
        semesterDao.upsertSemesters(semesterDataList.map(SemesterData::asEntity))

        for (semesterData in semesterDataList) {
            try {
                val chapelData = uSaintRemoteSource.remoteChapelData(
                    credential,
                    semesterData.year,
                    semesterData.semester
                )
                chapelDao.upsertChapel(chapelData.asEntity())
            } catch (e: Exception) {
                // 계절학기, 6회 수강 완료 등 채플 데이터가 없는 학기에 대해서는
                // 에러가 발생하지만 동작이 멈추면 안되고 다음 학기를 검색해야함
            }
        }
    }
}