package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistSongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlaylistSong(playlistSongEntity: PlaylistSongEntity)

    @Query("SELECT * FROM playlist_song_table WHERE trackId IN (:tracksIds) ORDER BY createAt DESC")
    fun getPlaylistSongsByIds(tracksIds: List<Long>): Flow<List<PlaylistSongEntity>>

    @Query("DELETE FROM playlist_song_table WHERE trackId = :trackId")
    suspend fun deletePlaylistSong(trackId: Long)
}