package com.denica.playlistmaker.search.domain.impl

import com.denica.playlistmaker.search.data.network.Resource
import com.denica.playlistmaker.search.domain.api.SongInteractor
import com.denica.playlistmaker.search.domain.api.SongRepository
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SongInteractorImpl(private val repository: SongRepository) : SongInteractor {


    override fun searchSong(expression: String): Flow<Pair<List<Song>?, String?>> {
        return repository.searchSong(expression).map { result ->
            when (result) {
                is Resource.Success<*> -> {
                    Pair(result.data, null)
                }

                is Resource.Error<*> -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}