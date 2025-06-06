package com.denica.playlistmaker.ui.search

import android.content.SharedPreferences
import com.denica.playlistmaker.domain.models.Song

import com.google.gson.Gson

const val SEARCH_HISTORY_KEY = "search_history_key"

class SearchHistory(val sharedPref: SharedPreferences) {

    fun clearHistory(savedTracksArrayList: ArrayList<Song>) {
        sharedPref.edit().putString(SEARCH_HISTORY_KEY, Gson().toJson(emptyArray<Song>())).apply()
        savedTracksArrayList.clear()

    }

    fun addTrack(savedTracksArrayList: ArrayList<Song>, songDto: Song):Int {
        if (songDto in savedTracksArrayList) {
            val position = savedTracksArrayList.indexOf(songDto)
            savedTracksArrayList.remove(songDto)
            savedTracksArrayList.add(0, songDto)
            return position
        } else {
            if (savedTracksArrayList.size >= 10) {
                savedTracksArrayList.removeAt(savedTracksArrayList.lastIndex)
                savedTracksArrayList.add(0, songDto)
            } else {
                savedTracksArrayList.add(0, songDto)
            }
        }
        return -1
    }

    fun write(savedTracksArrayList: ArrayList<Song>) {
        val json = Gson().toJson(savedTracksArrayList)
        sharedPref.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }

    fun read(): Array<Song> {
        val json = sharedPref.getString(SEARCH_HISTORY_KEY, null) ?: return emptyArray<Song>()
        val arrayFromJson = Gson().fromJson(json, Array<Song>::class.java)

        return arrayFromJson
    }

}