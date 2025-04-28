package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.yourssu.soomsil.usaint.proto.ReportCardSummaryProto
import java.io.InputStream
import java.io.OutputStream

object ReportCardSummarySerializer : Serializer<ReportCardSummaryProto> {
    override val defaultValue: ReportCardSummaryProto = ReportCardSummaryProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ReportCardSummaryProto {
        try {
            return ReportCardSummaryProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: ReportCardSummaryProto, output: OutputStream) {
        t.writeTo(output)
    }
}