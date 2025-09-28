package com.denica.playlistmaker.mediaLibrary.domain

import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DbSongInteractorImpl(val repository: DbSongRepository): DbSongInteractor {
    override suspend fun addSongToFavourite(song: Song) {
        repository.addSongToFavourite(song)
    }

    override suspend fun deleteSongFromFavourite(song: Song) {
        repository.deleteSongFromFavourite(song)
    }

    override fun getFavouriteSongs(): Flow<List<Song>> {
         return repository.getFavouriteSongs().map { it.reversed() }
    }

    override suspend fun getFavouriteSongsIds(): List<Long> {
        return repository.getFavouriteSongsIds()
    }
}