package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow

interface SongInteractor {
    fun searchSong(expression: String): Flow<Pair<List<Song>?, String?>>



}