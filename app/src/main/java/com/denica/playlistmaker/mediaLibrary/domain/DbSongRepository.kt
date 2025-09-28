package com.denica.playlistmaker.mediaLibrary.domain

import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface DbSongRepository {
    suspend fun addSongToFavourite(song: Song)

    suspend fun deleteSongFromFavourite(song: Song)

    fun getFavouriteSongs(): Flow<List<Song>>

    suspend fun getFavouriteSongsIds(): List<Long>
}