package com.denica.playlistmaker.mediaLibrary.ui.favouriteTracks

import com.denica.playlistmaker.search.domain.models.Song

sealed interface FavouriteTracksState {
    object Loading : FavouriteTracksState

    data class Content(
        val data: List<Song>
    ) : FavouriteTracksState

    object Empty : FavouriteTracksState
}