package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.domain.models.Song

interface SearchHistoryInteractor {

    suspend fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(song: Song): Int
    fun saveListToHistory(songs: List<Song>)

    interface HistoryConsumer {
        fun consume(searchHistory: List<Song>?)
    }
}