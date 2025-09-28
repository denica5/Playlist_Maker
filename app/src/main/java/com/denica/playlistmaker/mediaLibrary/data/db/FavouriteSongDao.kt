package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouriteSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToFavourite(song: FavouriteSongEntity)

    @Delete
    suspend fun deleteSongFromFavourite(song: FavouriteSongEntity)

    @Query("SELECT * FROM song_table")
    suspend fun getFavouriteSongs(): List<FavouriteSongEntity>

    @Query("SELECT trackId FROM song_table")
    suspend fun getFavouriteSongsIds(): List<Long>
}