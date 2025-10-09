package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    @TypeConverter
    fun fromTrackIdList(value: List<Long>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toTrackIdList(value: String): List<Long> {
        val type = object : TypeToken<List<Long>>() {}.type
        return Gson().fromJson(value, type)
    }
}