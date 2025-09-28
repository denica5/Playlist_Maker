package com.denica.playlistmaker.search.data.storage

import com.denica.playlistmaker.mediaLibrary.data.db.FavouriteSongDatabase
import com.denica.playlistmaker.search.data.network.Resource
import com.denica.playlistmaker.search.domain.api.SearchHistoryRepository
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Song>>,
    private val favouriteSongDatabase: FavouriteSongDatabase
) : SearchHistoryRepository {

    var ids = emptyList<Long>()
    override suspend fun saveListToHistory(songs: List<Song>) {
        getFavouriteIds()
        val savedSongs: ArrayList<Song> = arrayListOf()
        savedSongs.addAll(songs)
        songs.map { it.isFavourite = ids.contains(it.trackId) }
        storage.storeData(savedSongs)
    }

    override suspend fun saveToHistory(song: Song): Int {


        val savedSongs: ArrayList<Song> = storage.getData() ?: arrayListOf()
        val position = savedSongs.indexOfFirst { it.trackId == song.trackId }
        if (position != -1) {
            savedSongs.removeAt(position)
            savedSongs.add(0, song)
        } else {
            if (savedSongs.size >= 10) {
                savedSongs.removeAt(savedSongs.lastIndex)
            }
            savedSongs.add(0, song)
        }
        saveListToHistory(savedSongs)
        return position
    }

    override suspend fun getHistory(): Resource<List<Song>> {
        getFavouriteIds()
        val songs = storage.getData() ?: listOf()
        songs.map { it.isFavourite = ids.contains(it.trackId) }
        return Resource.Success(songs)
    }

    suspend fun getFavouriteIds() {
        withContext(Dispatchers.IO) {
            ids = favouriteSongDatabase.songDao().getFavouriteSongsIds()
        }
    }
}