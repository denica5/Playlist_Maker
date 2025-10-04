package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface PlaylistSongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlaylistSong(playlistSongEntity: PlaylistSongEntity)
}