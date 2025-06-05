package com.denica.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val SEARCH_HISTORY_KEY = "search_history_key"

class SearchHistory(val sharedPref: SharedPreferences) {

    fun clearHistory(savedTracksArrayList: ArrayList<TrackDto>) {
        sharedPref.edit().putString(SEARCH_HISTORY_KEY, Gson().toJson(emptyArray<TrackDto>())).apply()
        savedTracksArrayList.clear()

    }

    fun addTrack(savedTracksArrayList: ArrayList<TrackDto>, trackDto: TrackDto):Int {
        if (trackDto in savedTracksArrayList) {
            val position = savedTracksArrayList.indexOf(trackDto)
            savedTracksArrayList.remove(trackDto)
            savedTracksArrayList.add(0, trackDto)
            return position
        } else {
            if (savedTracksArrayList.size >= 10) {
                savedTracksArrayList.removeAt(savedTracksArrayList.lastIndex)
                savedTracksArrayList.add(0, trackDto)
            } else {
                savedTracksArrayList.add(0, trackDto)
            }
        }
        return -1
    }

    fun write(savedTracksArrayList: ArrayList<TrackDto>) {
        val json = Gson().toJson(savedTracksArrayList)
        sharedPref.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }

    fun read(): Array<TrackDto> {
        val json = sharedPref.getString(SEARCH_HISTORY_KEY, null) ?: return emptyArray<TrackDto>()
        val arrayFromJson = Gson().fromJson(json, Array<TrackDto>::class.java)

        return arrayFromJson
    }

}