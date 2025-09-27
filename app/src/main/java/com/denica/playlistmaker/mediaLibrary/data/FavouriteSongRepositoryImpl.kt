package com.denica.playlistmaker.mediaLibrary.data

import com.denica.playlistmaker.mediaLibrary.data.db.FavouriteSongDatabase
import com.denica.playlistmaker.mediaLibrary.data.db.FavouriteSongDbConverter
import com.denica.playlistmaker.mediaLibrary.data.db.FavouriteSongEntity
import com.denica.playlistmaker.mediaLibrary.domain.FavouriteSongRepository
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouriteSongRepositoryImpl(
    private val favouriteSongDatabase: FavouriteSongDatabase,
    private val favouriteSongDbConverter: FavouriteSongDbConverter

) : FavouriteSongRepository {
    override suspend fun addSongToFavourite(song: Song) {
        favouriteSongDatabase.songDao().addSongToFavourite(favouriteSongDbConverter.map(song))
    }

    override suspend fun deleteSongFromFavourite(song: Song) {
        favouriteSongDatabase.songDao().deleteSongFromFavourite(favouriteSongDbConverter.map(song))
    }

    override fun getFavouriteSongs(): Flow<List<Song>> = flow {
        val songs = favouriteSongDatabase.songDao().getFavouriteSongs()
        emit(convertFromFavouriteSongEntity(songs))
    }

    private fun convertFromFavouriteSongEntity(songs: List<FavouriteSongEntity>): List<Song> {
        return songs.map {
            favouriteSongDbConverter.map(it)
        }
    }
}