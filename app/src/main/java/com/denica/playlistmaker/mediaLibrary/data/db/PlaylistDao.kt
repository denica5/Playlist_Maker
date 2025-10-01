package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylistList(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId LIMIT 1")
    suspend fun getPlaylist(playlistId: Long): PlaylistEntity?

    @Transaction
    suspend fun addTrackToPlayList(playlistId: Long, trackId: Long) {
        val playlist = getPlaylist(playlistId) ?: return
        if (trackId in playlist.trackIds) return
        val updateTracksIds = playlist.trackIds + trackId
        val updatePlaylist = playlist.copy(
            trackIds = updateTracksIds,
            trackCount = updateTracksIds.size
        )
        updatePlaylist(updatePlaylist)
    }

    @Transaction
    suspend fun removeTrackToPlayList(playlistId: Long, trackId: Long) {
        val playlist = getPlaylist(playlistId) ?: return
        val updateTracksIds = playlist.trackIds.filter { it != trackId }
        val updatePlaylist = playlist.copy(
            trackIds = updateTracksIds,
            trackCount = updateTracksIds.size
        )
        updatePlaylist(updatePlaylist)
    }
}