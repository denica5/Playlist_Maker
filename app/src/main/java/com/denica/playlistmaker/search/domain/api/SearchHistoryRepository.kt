package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.data.network.Resource
import com.denica.playlistmaker.search.domain.models.Song

interface SearchHistoryRepository {
    fun saveListToHistory(songs: List<Song>)
    fun saveToHistory(song: Song)
    fun getHistory(): Resource<List<Song>>
}