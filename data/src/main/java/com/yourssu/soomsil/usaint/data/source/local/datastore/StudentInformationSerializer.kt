package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.yourssu.soomsil.usaint.proto.StudentInformation
import java.io.InputStream
import java.io.OutputStream

object StudentInformationSerializer : Serializer<StudentInformation> {
    override val defaultValue: StudentInformation = StudentInformation.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): StudentInformation {
        try {
            return StudentInformation.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: StudentInformation, output: OutputStream) {
        t.writeTo(output)
    }
}