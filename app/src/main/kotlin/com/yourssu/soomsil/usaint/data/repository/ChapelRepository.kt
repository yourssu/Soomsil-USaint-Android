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
    private val chapelAttendanceDao: ChapelAttendanceDao,
    private val uSaintRemoteSource: USaintRemoteSource,
) {
    val chapelCardData: Flow<ChapelSimpleData> =
        chapelDataSource.chapelCardData

    val chapelCardAttendanceData: Flow<List<ChapelAttendanceData>> =
        chapelAttendanceDao.getChapelAttendancesDivision()
            .map {
                it.map(ChapelAttendanceEntity::asExternalModel)
            }
    val semesterWithChapel: Flow<List<ChapelSimpleData>> =
        chapelDao.getChapelEntities().map { chapelEntityListMap ->
            chapelEntityListMap
                .map(ChapelEntity::asExternalModel)
        }

    val chapelWithAttendance: Flow<List<ChapelData>> =
        chapelAttendanceDao.getChapelAttendances().map { chapelEntityListMap ->
            chapelEntityListMap.map {
                ChapelData(it.key.asExternalModel(), it.value.map(ChapelAttendanceEntity::asExternalModel))
            }
        }

    suspend fun fetchChapelCardData(specifiedCurrentSemester: Pair<Int, SemesterType>): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val chapelCardData = uSaintRemoteSource.remoteChapelData(
            credential,
            specifiedCurrentSemester.first,
            specifiedCurrentSemester.second)

        chapelDataSource.setChapelCardData(chapelCardData.chapelSimpleData)
        chapelDao.upsertChapel(chapelCardData.chapelSimpleData.asEntity())
        chapelAttendanceDao.upsertChapelAttendances(chapelCardData.chapelAttendances.map(ChapelAttendanceData::asEntity))
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

                chapelDao.upsertChapel(chapelData.chapelSimpleData.asEntity())
                chapelAttendanceDao.upsertChapelAttendances(chapelData.chapelAttendances.map(ChapelAttendanceData::asEntity))
            } catch (e: Exception) {
                // 계절학기, 6회 수강 완료 등 채플 데이터가 없는 학기에 대해서는
                // 에러가 발생하지만 동작이 멈추면 안되고 다음 학기를 검색해야함
            }
        }
    }
}