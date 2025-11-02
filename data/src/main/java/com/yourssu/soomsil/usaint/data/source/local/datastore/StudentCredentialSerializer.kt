package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.yourssu.soomsil.usaint.proto.StudentCredentialProto
import java.io.InputStream
import java.io.OutputStream

object StudentCredentialSerializer : Serializer<StudentCredentialProto> {
    override val defaultValue: StudentCredentialProto = StudentCredentialProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): StudentCredentialProto {
        try {
            return StudentCredentialProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: StudentCredentialProto, output: OutputStream) {
        t.writeTo(output)
    }
}