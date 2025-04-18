package com.yourssu.soomsil.usaint.data.source.local.datastore

import androidx.datastore.core.Serializer
import com.yourssu.soomsil.usaint.proto.UserPreferences
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences
        get() = TODO("Not yet implemented")

    override suspend fun readFrom(input: InputStream): UserPreferences {
        TODO("Not yet implemented")
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        TODO("Not yet implemented")
    }
}