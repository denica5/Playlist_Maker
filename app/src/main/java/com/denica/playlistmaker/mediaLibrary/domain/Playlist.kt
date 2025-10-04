package com.denica.playlistmaker.mediaLibrary.domain

class Playlist(
    val id: Long = 0L,
    val name: String,
    val description: String,
    val imagePath: String,
    val trackIds: List<Long> = emptyList(),
    val trackCount: Int = 0
)