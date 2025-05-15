package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.DataStore
import com.yourssu.soomsil.usaint.core.model.ChapelSimpleData
import com.yourssu.soomsil.usaint.core.types.SemesterType
import com.yourssu.soomsil.usaint.proto.ChapelDataProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ChapelDataSource @Inject constructor (
    private val chapelDataSource: DataStore<ChapelDataProto>
)  {
    val chapelCardData: Flow<ChapelSimpleData> = chapelDataSource.data
        .map {
            ChapelSimpleData(
                year = it.year,
                semester = SemesterType.One,
                division = it.division,
                chapelRoom = it.chapelRoom,
                chapelTime = it.chapelTime,
                floorLevel = it.floorLevel,
                seatNumber = it.seatNumber,
                absenceTime = it.absenceTime,
                result = it.result
            )
        }

    suspend fun setChapelCardData(chapelSimpleData: ChapelSimpleData) {
        try {
            chapelDataSource.updateData {
                ChapelDataProto.newBuilder()
                    .setDivision(chapelSimpleData.division)
                    .setAbsenceTime(chapelSimpleData.absenceTime)
                    .setChapelRoom(chapelSimpleData.chapelRoom)
                    .setChapelTime(chapelSimpleData.chapelTime)
                    .setFloorLevel(chapelSimpleData.floorLevel)
                    .setResult(chapelSimpleData.result)
                    .setSemester(chapelSimpleData.semester.name)
                    .setSeatNumber(chapelSimpleData.seatNumber)
                    .setYear(chapelSimpleData.year)
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