package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import com.yourssu.soomsil.usaint.proto.ChapelDataProto

object ChapelDataSerializer : Serializer<ChapelDataProto> {
    override val defaultValue: ChapelDataProto = ChapelDataProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ChapelDataProto {
        try {
            return ChapelDataProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ChapelDataProto, output: OutputStream) {
        t.writeTo(output)
    }
}