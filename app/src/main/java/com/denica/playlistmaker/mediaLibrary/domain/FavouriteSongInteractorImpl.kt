package com.denica.playlistmaker.mediaLibrary.domain

import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteSongInteractorImpl(val repository: FavouriteSongRepository): FavouriteSongInteractor {
    override suspend fun addSongToFavourite(song: Song) {
        repository.addSongToFavourite(song)
    }

    override suspend fun deleteSongFromFavourite(song: Song) {
        repository.deleteSongFromFavourite(song)
    }

    override fun getFavouriteSongs(): Flow<List<Song>> {
         return repository.getFavouriteSongs().map { it.reversed() }
    }
}