package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.domain.models.Song

interface SearchHistoryInteractor {

    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(songs: List<Song>)

    interface HistoryConsumer {
        fun consume(searchHistory: List<Song>?)
    }
}