package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(version = 1, entities = [SongEntity::class, PlaylistEntity::class])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
}