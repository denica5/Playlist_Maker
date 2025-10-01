package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0L,
    val name: String,
    val description: String,
    val imagePath: String,
    val trackIds: List<Long> = emptyList(),
    val trackCount: Int = 0
)
