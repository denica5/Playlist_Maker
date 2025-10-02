package com.denica.playlistmaker.mediaLibrary.ui.playlist

import com.denica.playlistmaker.mediaLibrary.domain.Playlist

sealed interface PlaylistState {
    object Loading : PlaylistState

    data class Content(
        val data: List<Playlist>
    ) : PlaylistState

    object Empty : PlaylistState
}