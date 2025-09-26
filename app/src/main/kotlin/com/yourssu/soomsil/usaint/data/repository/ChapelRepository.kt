package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.ChapelAttendanceData
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.model.SemesterData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.source.local.dao.ChapelDao
import com.yourssu.soomsil.usaint.data.source.local.dao.SemesterDao
import com.yourssu.soomsil.usaint.data.source.local.datastore.ChapelDataSource
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.data.source.local.entity.ChapelAttendanceEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.asEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.asExternalModel
import com.yourssu.soomsil.usaint.data.source.remote.USaintRemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ChapelRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val semesterDao: SemesterDao,
    private val chapelDao: ChapelDao,
    private val uSaintRemoteSource: USaintRemoteSource,
    private val chapelDataSource: ChapelDataSource
) {

    // 채플DB에서 지금까지 불러왔던 모든 채플 데이터(현재학기 + 이전학기)를 불러옵니다.
    val chapels: Flow<List<ChapelData>> = // 전체 채플 정보
        combine(
            chapelDao.getAllChapels(),
            chapelDao.getAllAttendances()
        ) { chapels, attendances ->
            val attendancesMap = attendances.groupBy {
                Triple(it.year, it.semester, it.division)
            }

            chapels.map { chapel ->
                val compositeKey = Triple(chapel.year, chapel.semester, chapel.division)
                ChapelData(
                    chapelSimpleData = chapel.asExternalModel(),
                    chapelAttendances = attendancesMap[compositeKey]?.map(ChapelAttendanceEntity::asExternalModel)
                        ?: emptyList()
                )
            }
        }

    // getCurrentSemesterUseCase를 사용하기가 어려워서
    // 전체 채플 데이터에서 DataStore에 저장한 분반 정보를 비교하여 뽑아냅니다.
    val chapelCard: Flow<ChapelData?> =
        combine(chapels, chapelDataSource.chapelCardData) { chapelList, cardData ->
            chapelList.find {
                it.chapelSimpleData.year == cardData.year &&
                        it.chapelSimpleData.semester == cardData.semester
            }
        }

    // 앱이 최초에 실행될 때 이 함수가 먼저 실행됩니다.
    // 채플 DB와 DataStore에 불러온 값을 저장합니다.
    suspend fun fetchChapelCardData(specifiedCurrentSemester: Pair<Int, SemesterType>): Result<Unit> =
        runCatching {
            val credential = studentCredential.getStudentCredential()
            val chapelCardData = uSaintRemoteSource.remoteChapelData(
                credential,
                specifiedCurrentSemester.first,
                specifiedCurrentSemester.second
            )

            chapelDataSource.setChapelCardData(chapelSimpleData = chapelCardData.chapelSimpleData)
            chapelDao.upsertChapel(chapelCardData.chapelSimpleData.asEntity())
            chapelDao.upsertChapelAttendances(
                chapelCardData.chapelAttendances.map(
                    ChapelAttendanceData::asEntity
                )
            )
        }

    // 현재 학기를 제외한 이수한 학기의 채플 정보를 모두 불러옵니다.
    // 채플 DB에 불러온 값을 저장합니다.
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
                chapelDao.upsertChapelAttendances(
                    chapelData.chapelAttendances.map(
                        ChapelAttendanceData::asEntity
                    )
                )
            } catch (e: Exception) {
                // 계절학기, 6회 수강 완료 등 채플 데이터가 없는 학기에 대해서는
                // 에러가 발생하지만 동작이 멈추면 안되고 다음 학기를 검색해야함
            }
        }
    }

    suspend fun deleteAll() {
        chapelDao.deleteAllChapel()
        chapelDao.deleteAllAttendance()
        chapelDataSource.clear()
    }
}