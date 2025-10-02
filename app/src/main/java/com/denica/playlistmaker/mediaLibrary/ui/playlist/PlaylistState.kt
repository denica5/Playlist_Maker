package com.denica.playlistmaker.mediaLibrary.ui.playlist

import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaLibrary.ui.favouriteTracks.FavouriteTracksState
import com.denica.playlistmaker.search.domain.models.Song

sealed interface PlaylistState {
    object Loading : PlaylistState

    data class Content(
        val data: List<Playlist>
    ) : PlaylistState

    object Empty : PlaylistState
}