package com.denica.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import java.lang.reflect.Type
import androidx.core.content.edit

class PrefsStorageClient<T>(
    private val dataKey: String,
    private val type: Type,
    private val gson: Gson,
    private val prefs: SharedPreferences
) : StorageClient<T> {



    override fun storeData(data: T) {
        prefs.edit {
            putString(dataKey, gson.toJson(data, type))
        }
    }


    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null)
        return if (dataJson == null) {
            null
        } else {
            gson.fromJson(dataJson, type)
        }
    }
}