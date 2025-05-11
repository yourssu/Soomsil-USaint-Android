package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import com.yourssu.soomsil.usaint.proto.ChapelAttendanceDataProto

object ChapelAttendanceDataSerializer : Serializer<ChapelAttendanceDataProto> {
    override val defaultValue: ChapelAttendanceDataProto = ChapelAttendanceDataProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ChapelAttendanceDataProto {
        try {
            return ChapelAttendanceDataProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ChapelAttendanceDataProto, output: OutputStream) {
        t.writeTo(output)
    }
}