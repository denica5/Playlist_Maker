package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.data.Resource
import com.denica.playlistmaker.search.domain.models.Song

interface SongRepository {
    fun searchSong(expression: String): Resource<List<Song>>
}