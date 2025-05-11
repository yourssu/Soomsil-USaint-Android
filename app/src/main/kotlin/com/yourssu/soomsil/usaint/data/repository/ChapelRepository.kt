package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.ChapelAttendanceData
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.model.ChapelSimpleData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.source.local.dao.ChapelAttendanceDao
import com.yourssu.soomsil.usaint.data.source.local.dao.ChapelDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.datastore.ChapelDataSource
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.data.source.local.entity.ChapelAttendanceEntity
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
    private val chapelAttendanceDao: ChapelAttendanceDao,
    private val uSaintRemoteSource: USaintRemoteSource,
) {
    val chapelCardData: Flow<ChapelSimpleData> =
        chapelDataSource.chapelCardData

    val semesterWithChapel: Flow<Map<SemesterData, ChapelSimpleData>> =
        semesterDao.getSemesterWithChapel().map { semesterEntityListMap ->
            semesterEntityListMap
                .mapKeys { (semesterEntity, _) -> semesterEntity.asExternalModel() }
                .mapValues { (_, chapelEntity) -> chapelEntity.asExternalModel() }
        }

    val chapelWithAttendance: Flow<List<ChapelData>> =
        chapelAttendanceDao.getChapelAttendances().map { chapelEntityListMap ->
            chapelEntityListMap.map {
                ChapelData(it.key.asExternalModel(), it.value.map(ChapelAttendanceEntity::asExternalModel))
            }
        }

    suspend fun fetchChapelCardData(): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        // TODO 현재 연도하고 학기를 뭘로 가져와야 하는거지???
        val chapelCardData = uSaintRemoteSource.remoteChapelData(credential, 2025, SemesterType.One)
        chapelCardData.chapelSimpleData.totalAttendance = chapelCardData.chapelAttendances.size
        chapelCardData.chapelSimpleData.currentAttendance =
            chapelCardData.chapelAttendances.filter {
                it.attendance == "출석"
            }.size
        chapelDataSource.setChapelCardData(chapelCardData.chapelSimpleData)
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

                chapelData.chapelSimpleData.totalAttendance = chapelData.chapelAttendances.size
                chapelData.chapelSimpleData.currentAttendance = chapelData.chapelAttendances.filter {
                    it.attendance == "출석"
                }.size

                chapelDao.upsertChapel(chapelData.chapelSimpleData.asEntity())
                chapelAttendanceDao.upsertChapelAttendances(chapelData.chapelAttendances.map(ChapelAttendanceData::asEntity))
            } catch (e: Exception) {
                // 계절학기, 6회 수강 완료 등 채플 데이터가 없는 학기에 대해서는
                // 에러가 발생하지만 동작이 멈추면 안되고 다음 학기를 검색해야함
            }
        }
    }
}