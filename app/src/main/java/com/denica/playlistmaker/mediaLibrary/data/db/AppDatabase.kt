package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [SongEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}