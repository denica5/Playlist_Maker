package com.denica.playlistmaker.domain.api

import com.denica.playlistmaker.domain.models.Song

interface SongRepository {
    fun searchSong(expression: String): Pair<List<Song>, Boolean>
}