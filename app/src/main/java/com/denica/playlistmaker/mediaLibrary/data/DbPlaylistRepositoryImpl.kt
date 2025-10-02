package com.denica.playlistmaker.mediaLibrary.data

import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistDao
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistDbConverter
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistEntity
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistRepository
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DbPlaylistRepositoryImpl(
    val playlistDao: PlaylistDao,
    val playlistDbConverter: PlaylistDbConverter
) :
    DbPlaylistRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlistDbConverter.map(playlist))
    }

    override fun getPlaylistList(): Flow<List<Playlist>> = flow {
        val playlists = playlistDao.getPlaylistList()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun getPlaylist(playlistId: Long): Playlist? {
        return playlistDbConverter.map(
            playlistDao.getPlaylist(playlistId) ?: PlaylistEntity(
                -1,
                "",
                "",
                ""
            )
        )

    }

    override suspend fun addTrackToPlayList(playlistId: Long, trackId: Long): Int {
        return playlistDao.addTrackToPlayList(playlistId, trackId)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map {
            playlistDbConverter.map(it)
        }
    }
}