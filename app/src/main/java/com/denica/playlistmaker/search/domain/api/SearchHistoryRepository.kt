package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.data.network.Resource
import com.denica.playlistmaker.search.domain.models.Song

interface SearchHistoryRepository {
    suspend fun saveListToHistory(songs: List<Song>)
    suspend fun saveToHistory(song: Song): Int
    suspend fun getHistory(): Resource<List<Song>>
}