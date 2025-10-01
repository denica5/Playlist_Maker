package com.denica.playlistmaker.mediaLibrary.domain

import kotlinx.coroutines.flow.Flow

class DbPlaylistInteractorImpl(val repository: DbPlaylistRepository) : DbPlaylistInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        repository.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override fun getPlaylistList(): Flow<List<Playlist>> {
        return repository.getPlaylistList()
    }

    override suspend fun getPlaylist(playlistId: Long): Playlist? {
        return repository.getPlaylist(playlistId)
    }

    override suspend fun addTrackToPlayList(playlistId: Long, trackId: Long) {
        repository.addTrackToPlayList(playlistId, trackId)
    }

    override suspend fun removeTrackToPlayList(playlistId: Long, trackId: Long) {
        repository.removeTrackToPlayList(playlistId, trackId)
    }
}