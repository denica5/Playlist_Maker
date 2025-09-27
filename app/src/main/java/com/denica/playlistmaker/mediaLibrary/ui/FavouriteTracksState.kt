package com.denica.playlistmaker.mediaLibrary.ui

import com.denica.playlistmaker.search.domain.models.Song

sealed interface FavouriteTracksState {
    object Loading : FavouriteTracksState

    data class Content(
        val data: List<Song>
    ) : FavouriteTracksState

    data class Empty(
        val message: String
    ) : FavouriteTracksState
}