package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [FavouriteSongEntity::class])
abstract class FavouriteSongDatabase : RoomDatabase() {
    abstract fun songDao(): FavouriteSongDao
}