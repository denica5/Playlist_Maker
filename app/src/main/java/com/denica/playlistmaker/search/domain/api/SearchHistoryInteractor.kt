package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.domain.models.Song

interface SearchHistoryInteractor {

    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(song: Song)
    fun saveListToHistory(songs: List<Song>)

    interface HistoryConsumer {
        fun consume(searchHistory: List<Song>?)
    }
}