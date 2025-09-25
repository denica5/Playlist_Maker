package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.data.network.Resource
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun searchSong(expression: String): Flow<Resource<List<Song>>>
}