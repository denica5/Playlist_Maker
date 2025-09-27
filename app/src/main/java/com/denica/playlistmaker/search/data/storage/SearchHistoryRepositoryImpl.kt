package com.denica.playlistmaker.search.data.storage

import com.denica.playlistmaker.mediaLibrary.data.db.FavouriteSongDatabase
import com.denica.playlistmaker.search.data.network.Resource
import com.denica.playlistmaker.search.domain.api.SearchHistoryRepository
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.coroutineScope

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Song>>,
    private val favouriteSongDatabase: FavouriteSongDatabase
) : SearchHistoryRepository {

    override suspend fun saveListToHistory(songs: List<Song>) {
        val savedSongs: ArrayList<Song> = arrayListOf()
        savedSongs.addAll(songs)
        val ids = getFavouriteIds()
        songs.map { it.isFavourite = ids.contains(it.trackId) }
        storage.storeData(savedSongs)
    }

    override suspend fun saveToHistory(song: Song): Int {
        var position = -1
        val savedSongs: ArrayList<Song> = storage.getData() ?: arrayListOf()
        if (savedSongs.contains(song)) {
            position = savedSongs.indexOf(song)
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
        return position
    }

    override suspend fun getHistory(): Resource<List<Song>> {
        val ids = getFavouriteIds()
        val songs = storage.getData() ?: listOf()
        songs.map { it.isFavourite = ids.contains(it.trackId) }
        return Resource.Success(songs)
    }

    suspend fun getFavouriteIds(): List<Long> {
        return favouriteSongDatabase.songDao().getFavouriteSongsIds()
    }
}