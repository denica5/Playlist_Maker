package com.denica.playlistmaker.search.data

import com.denica.playlistmaker.search.domain.api.SearchHistoryRepository
import com.denica.playlistmaker.search.domain.models.Song

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Song>>
) : SearchHistoryRepository {

    override fun saveToHistory(songs: List<Song>) {
        val savedSongs: ArrayList<Song> = arrayListOf()
        savedSongs.addAll(songs)
        storage.storeData(savedSongs)
    }

    override fun getHistory(): Resource<List<Song>> {
        val movies = storage.getData() ?: listOf()
        return Resource.Success(movies)
    }
}