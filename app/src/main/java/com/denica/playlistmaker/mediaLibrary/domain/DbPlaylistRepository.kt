package com.denica.playlistmaker.mediaLibrary.domain

import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface DbPlaylistRepository {
    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)


    suspend fun deletePlaylist(playlist: Playlist)


    fun getPlaylistList(): Flow<List<Playlist>>

    suspend fun addPlaylistSong(playlistSong: Song)
    suspend fun getPlaylist(playlistId: Long): Playlist?


    suspend fun addTrackToPlayList(playlistId: Long, trackId: Long): Int

    suspend fun removeTrackToPlayList(playlistId: Long, trackId: Long) {

    }
}