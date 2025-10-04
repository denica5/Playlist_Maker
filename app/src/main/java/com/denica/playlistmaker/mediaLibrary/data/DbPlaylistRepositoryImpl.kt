package com.denica.playlistmaker.mediaLibrary.data

import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistDao
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistDbConverter
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistEntity
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistSongDao
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistSongDbConverter
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistRepository
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DbPlaylistRepositoryImpl(
    val playlistDao: PlaylistDao,
    val playlistSongDao: PlaylistSongDao,
    val playlistDbConverter: PlaylistDbConverter,
    val playlistSongDbConverter: PlaylistSongDbConverter
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

    override fun getPlaylistList(): Flow<List<Playlist>> =
        playlistDao.getPlaylistList().map { convertFromPlaylistEntity(it) }

    override suspend fun addPlaylistSong(playlistSong: Song) {
        playlistSongDao.addPlaylistSong(playlistSongDbConverter.map(playlistSong))
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