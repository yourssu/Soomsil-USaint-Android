package com.yourssu.soomsil.usaint.data.source.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapTypeConverter {
    @TypeConverter
    fun fromString(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: Map<String, String>): String {
        return Gson().toJson(map)
    }
}