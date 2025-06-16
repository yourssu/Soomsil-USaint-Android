package com.yourssu.soomsil.usaint.data.repository

import com.yourssu.soomsil.usaint.core.model.LectureData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.data.source.local.dao.LectureDao
import com.yourssu.soomsil.usaint.data.source.local.datastore.StudentCredentialDataSource
import com.yourssu.soomsil.usaint.data.source.local.entity.asEntity
import com.yourssu.soomsil.usaint.data.source.local.entity.asExternalModel
import com.yourssu.soomsil.usaint.data.source.remote.USaintRemoteSource
import javax.inject.Inject

class LectureRepository @Inject constructor(
    private val studentCredential: StudentCredentialDataSource,
    private val lectureDao: LectureDao,
    private val remoteSource: USaintRemoteSource
){
    suspend fun getLocalLectures(year: Int, semester: SemesterType): Result<List<LectureData>> = runCatching {
        lectureDao.getLectureEntitiesForSemester(year, semester.name)
            .map { it.asExternalModel() }
    }

    suspend fun getRemoteLectures(year: Int, semester: SemesterType): Result<List<LectureData>> = runCatching {
        remoteSource.remoteLectureDataList(studentCredential.getStudentCredential(), year, semester)
    }

    suspend fun storeLectures(vararg lectures: LectureData): Result<Unit> {
        return runCatching {
            lectureDao.upsertLectures(lectures.map(LectureData::asEntity))
        }
    }
}