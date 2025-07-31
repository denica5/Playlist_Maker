package com.denica.playlistmaker.search.domain.api

import com.denica.playlistmaker.search.domain.models.Song

interface SongInteractor {
    fun searchSong(expression: String, consumer: SongConsumer)

    interface SongConsumer {
        fun consume(foundSongs: List<Song>?, errorMessage: String?)
    }

}