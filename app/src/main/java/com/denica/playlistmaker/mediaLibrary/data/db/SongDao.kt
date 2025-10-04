package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToFavourite(song: SongEntity)

    @Delete
    suspend fun deleteSongFromFavourite(song: SongEntity)

    @Query("SELECT * FROM song_table ORDER BY createAt ASC")
     fun getFavouriteSongs(): Flow<List<SongEntity>>

    @Query("SELECT trackId FROM song_table")
    suspend fun getFavouriteSongsIds(): List<Long>
}