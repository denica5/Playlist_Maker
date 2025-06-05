package com.denica.playlistmaker.domain.api

import com.denica.playlistmaker.domain.models.Song

interface SongInteractor {
    fun searchSong(expression: String, consumer: SongConsumer)

    interface SongConsumer {
        fun consume(foundSongs: Pair<List<Song>,Boolean>)
    }

}