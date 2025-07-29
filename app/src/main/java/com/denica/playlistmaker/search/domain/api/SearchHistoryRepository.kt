package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.data.Resource
import com.denica.playlistmaker.search.domain.models.Song

interface SearchHistoryRepository {
    fun saveToHistory(songs: List<Song>)
    fun getHistory(): Resource<List<Song>>
}