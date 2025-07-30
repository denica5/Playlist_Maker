package com.denica.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.denica.playlistmaker.search.data.StorageClient
import com.denica.playlistmaker.settings.ui.PLAYLIST_MAKER_PREFERENCES
import com.google.gson.Gson
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val context: Context,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    private val gson = Gson()
    override fun storeData(data: T) {
        prefs.edit()
            .putString(dataKey, gson.toJson(data, type))
            .apply()
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