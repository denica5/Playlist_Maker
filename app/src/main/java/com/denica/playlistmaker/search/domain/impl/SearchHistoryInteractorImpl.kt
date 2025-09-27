package com.denica.playlistmaker.search.domain.impl

import com.denica.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.denica.playlistmaker.search.domain.api.SearchHistoryRepository
import com.denica.playlistmaker.search.domain.models.Song

class SearchHistoryInteractorImpl(
    val repository: SearchHistoryRepository
) : SearchHistoryInteractor {
    override suspend fun getHistory(consumer: SearchHistoryInteractor.HistoryConsumer) {
        consumer.consume(repository.getHistory().data)
    }

    override suspend fun saveToHistory(song: Song): Int {
        return repository.saveToHistory(song)
    }

    override suspend fun saveListToHistory(songs: List<Song>) {
        repository.saveListToHistory(songs)
    }
}