package com.denica.playlistmaker.search.data.storage

import com.denica.playlistmaker.search.data.network.Resource
import com.denica.playlistmaker.search.domain.api.SearchHistoryRepository
import com.denica.playlistmaker.search.domain.models.Song

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Song>>
) : SearchHistoryRepository {

    override fun saveListToHistory(songs: List<Song>) {
        val savedSongs: ArrayList<Song> = arrayListOf()
        savedSongs.addAll(songs)
        storage.storeData(savedSongs)
    }

    override fun saveToHistory(song: Song) {
        val savedSongs: ArrayList<Song> = storage.getData() ?: arrayListOf()
        if (savedSongs.contains(song)) {
            savedSongs.remove(song)
            savedSongs.add(0, song)
        } else {
            if (savedSongs.size >= 10) {
                savedSongs.removeAt(savedSongs.lastIndex)
                savedSongs.add(0, song)
            } else {
                savedSongs.add(0, song)
            }
        }
        saveListToHistory(savedSongs)
    }

    override fun getHistory(): Resource<List<Song>> {
        val movies = storage.getData() ?: listOf()
        return Resource.Success(movies)
    }
}