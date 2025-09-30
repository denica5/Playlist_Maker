package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.domain.models.Song

interface SearchHistoryInteractor {

    suspend fun getHistory(consumer: HistoryConsumer)
    suspend fun saveToHistory(song: Song): Int
    suspend fun saveListToHistory(songs: List<Song>)

    interface HistoryConsumer {
        fun consume(searchHistory: List<Song>?)
    }
}