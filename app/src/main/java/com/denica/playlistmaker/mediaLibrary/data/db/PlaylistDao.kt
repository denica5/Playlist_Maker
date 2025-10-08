package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("SELECT * FROM playlist_table")
    fun getPlaylistList(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId LIMIT 1")
    suspend fun getPlaylist(playlistId: Long): PlaylistEntity?

    @Transaction
    suspend fun addTrackToPlayList(playlistId: Long, trackId: Long): Int {
        val playlist = getPlaylist(playlistId) ?: return -1
        if (trackId in playlist.trackIds) {

            return 0
        }
        val updateTracksIds = playlist.trackIds + trackId
        val updatePlaylist = playlist.copy(
            trackIds = updateTracksIds,
            trackCount = updateTracksIds.size
        )
        updatePlaylist(updatePlaylist)
        return 1
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