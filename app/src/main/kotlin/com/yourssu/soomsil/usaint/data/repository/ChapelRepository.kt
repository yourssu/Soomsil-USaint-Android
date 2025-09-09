package com.yourssu.soomsil.usaint.data.repository

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
        chapelDao.getChapels().map { chapelList ->
            chapelList
                .map {
                    ChapelData(
                        it.chapel.asExternalModel(),
                        it.attendances.map(ChapelAttendanceEntity::asExternalModel)
                    )
                }
        }

    // getCurrentSemesterUseCase를 사용하기가 어려워서
    // 전체 채플 데이터에서 DataStore에 저장한 분반 정보를 비교하여 뽑아냅니다.
    val chapelCard: Flow<ChapelData?> = // 현재 학기 설정에 따른 카드에 표시할 채플정보
            chapels.map { chapelList ->
                chapelList.find {
                    val savedCardData = chapelDataSource.chapelCardData.first()

                    it.chapelSimpleData.division == savedCardData.division
                            // 채플 주차별 정보에서 첫 수업일자를 가져와서 설정한 현재연도와 일치하는지 확인
                            // 해당학기 채플 주차별 데이터가 불러와지기 전 chapelCard를 호출하는 경우 ArrayIndexOutOfBoundsException
                            && (it.chapelAttendances.any() && it.chapelAttendances[0].classDate.startsWith(savedCardData.year.toString()))
                            // 수업 월이 7월 이전이면 1학기 채플인지 확인하고 8월 이후면 2학기 채플인것을 확인하고 현재 채플 정보로 반영
                            // (같은 학년도에 같은 분반으로 1,2학기 수업을 들을 경우 여기서 걸러져야 함)
                            && ((it.chapelAttendances[0].classDate.substring(5, 7).toInt() <= 7 && savedCardData.semester == SemesterType.One)
                                || (it.chapelAttendances[0].classDate.substring(5, 7).toInt() > 7 && savedCardData.semester == SemesterType.Two))
                }
            }

    // 앱이 최초에 실행될 때 이 함수가 먼저 실행됩니다.
    // 채플 DB와 DataStore에 불러온 값을 저장합니다.
    suspend fun fetchChapelCardData(specifiedCurrentSemester: Pair<Int, SemesterType>): Result<Unit> = runCatching {
        val credential = studentCredential.getStudentCredential()
        val chapelCardData = uSaintRemoteSource.remoteChapelData(
            credential,
            specifiedCurrentSemester.first,
            specifiedCurrentSemester.second
        )

        // TODO 기존 채플 과목코드에 대해 지우는 코드입니다. 나중에 삭제해주세요
        chapelDao.deleteChapelAttendancesEntitiesWithDivision(chapelCardData.chapelSimpleData.division)
        chapelDao.deleteChapelEntityWithDivision(chapelCardData.chapelSimpleData.division)
        // ---

        chapelDataSource.setChapelCardData(chapelSimpleData = chapelCardData.chapelSimpleData)
        chapelDao.upsertChapel(chapelCardData.chapelSimpleData.asEntity())
        chapelDao.upsertChapelAttendances(chapelCardData.chapelAttendances.map {
            it.asEntity(specifiedCurrentSemester.first, specifiedCurrentSemester.second)
        })
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

                // TODO 기존 채플 과목코드에 대해 지우는 코드입니다. 나중에 삭제해주세요
                chapelDao.deleteChapelAttendancesEntitiesWithDivision(chapelData.chapelSimpleData.division)
                chapelDao.deleteChapelEntityWithDivision(chapelData.chapelSimpleData.division)
                // ---

                chapelDao.upsertChapel(chapelData.chapelSimpleData.asEntity())
                chapelDao.upsertChapelAttendances(chapelData.chapelAttendances.map {
                    it.asEntity(semesterData.year, semesterData.semester)
                })
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