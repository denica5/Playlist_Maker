package com.denica.playlistmaker.search.domain.impl

import com.denica.playlistmaker.search.data.Resource
import com.denica.playlistmaker.search.domain.api.SongInteractor
import com.denica.playlistmaker.search.domain.api.SongRepository
import java.util.concurrent.Executors

class SongInteractorImpl(private val repository: SongRepository) : SongInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchSong(expression: String, consumer: SongInteractor.SongConsumer) {
        executor.execute {
            when(val resource = repository.searchSong(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }
}