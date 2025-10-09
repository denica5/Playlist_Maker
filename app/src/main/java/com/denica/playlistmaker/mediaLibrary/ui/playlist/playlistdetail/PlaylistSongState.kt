package com.denica.playlistmaker.mediaLibrary.ui.playlist.playlistdetail

import com.denica.playlistmaker.search.domain.models.Song

sealed interface PlaylistSongState {

    object Loading : PlaylistSongState

    data class Content(
        val data: List<Song>,
        val allTracksDuration: String
    ) : PlaylistSongState

    object Empty : PlaylistSongState

}