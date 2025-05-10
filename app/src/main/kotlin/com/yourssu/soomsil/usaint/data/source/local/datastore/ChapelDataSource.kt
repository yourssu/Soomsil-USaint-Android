package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import com.yourssu.soomsil.usaint.core.model.ChapelData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import com.yourssu.soomsil.usaint.proto.ChapelDataProto
import java.io.IOException
import javax.inject.Inject

class ChapelDataSource @Inject constructor (
    private val chapelDataSource: DataStore<ChapelDataProto>
)  {
    val chapelCardData: Flow<ChapelData> = chapelDataSource.data
        .map {
            ChapelData(
                year = it.year,
                semester = SemesterType.One,
                division = it.division,
                chapelRoom = it.chapelRoom,
                chapelTime = it.chapelTime,
                floorLevel = it.floorLevel,
                seatNumber = it.seatNumber,
                absenceTime = it.absenceTime,
                result = it.result,
            )
        }

    suspend fun setChapelCardData(chapelData: ChapelData) {
        try {
            chapelDataSource.updateData {
                ChapelDataProto.newBuilder()
                    .setDivision(chapelData.division)
                    .setAbsenceTime(chapelData.absenceTime)
                    .setChapelRoom(chapelData.chapelRoom)
                    .setChapelTime(chapelData.chapelTime)
                    .setFloorLevel(chapelData.floorLevel)
                    .setResult(chapelData.result)
                    .setSemester(chapelData.semester.name)
                    .setSeatNumber(chapelData.seatNumber)
                    .setYear(chapelData.year)
                    .build()
            }
        } catch (e: IOException) {
            Timber.e("Failed to update Chapel card data", e)
        }
    }

    suspend fun clear() {
        try {
            chapelDataSource.updateData { ChapelDataProto.getDefaultInstance() }
        } catch (e: IOException) {
            Timber.e("Failed to clear Chapel card data", e)
        }
    }
}