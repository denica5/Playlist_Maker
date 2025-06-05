package com.denica.playlistmaker.domain.impl

import com.denica.playlistmaker.domain.api.SongInteractor
import com.denica.playlistmaker.domain.api.SongRepository
import java.util.concurrent.Executors

class SongInteractorImpl(private val repository: SongRepository): SongInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchSong(expression: String, consumer: SongInteractor.SongConsumer) {
        executor.execute{ consumer.consume(repository.searchSong(expression)) }
    }
}