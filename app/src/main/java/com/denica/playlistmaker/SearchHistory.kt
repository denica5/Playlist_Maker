package com.denica.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

const val SEARCH_HISTORY_KEY = "search_history_key"

class SearchHistory(val sharedPref: SharedPreferences) {

    fun clearHistory(savedTracksArrayList: ArrayList<Track>) {
        sharedPref.edit().putString(SEARCH_HISTORY_KEY, Gson().toJson(emptyArray<Track>())).apply()
        savedTracksArrayList.clear()

    }

    fun addTrack(savedTracksArrayList: ArrayList<Track>, track: Track):Int {
        if (track in savedTracksArrayList) {
            val position = savedTracksArrayList.indexOf(track)
            savedTracksArrayList.remove(track)
            savedTracksArrayList.add(0, track)
            return position
        } else {
            if (savedTracksArrayList.size >= 10) {
                savedTracksArrayList.removeAt(savedTracksArrayList.lastIndex)
                savedTracksArrayList.add(0, track)
            } else {
                savedTracksArrayList.add(0, track)
            }
        }
        return -1
    }

    fun write(savedTracksArrayList: ArrayList<Track>) {
        val json = Gson().toJson(savedTracksArrayList)
        sharedPref.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }

    fun read(): Array<Track> {
        val json = sharedPref.getString(SEARCH_HISTORY_KEY, null) ?: return emptyArray<Track>()
        val arrayFromJson = Gson().fromJson(json, Array<Track>::class.java)

        return arrayFromJson
    }

}