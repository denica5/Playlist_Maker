package com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.R
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistInteractor
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistViewModel(val playlistInteractor: DbPlaylistInteractor) : ViewModel() {

    private val playlistState: MutableStateFlow<PlaylistState> =
        MutableStateFlow(PlaylistState.Empty)

    fun observePlaylistState() = playlistState.asStateFlow()

    init {
    }

    fun getPlaylists() {

        viewModelScope.launch {
            renderState(PlaylistState.Loading)
            playlistInteractor.getPlaylistList().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistState.Empty)
        } else {
            renderState(PlaylistState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistState) {
        playlistState.update { state }
    }

    companion object {
        fun pluralizeTracks(tracksCounts: Int): Int {
            val lastTwo = tracksCounts % 100
            val lastOne = tracksCounts % 10

            return when {
                lastTwo in 11..19 -> {
                    R.string.playlist_track_pluralize_11_19
                }

                lastOne == 1 -> {
                    R.string.playlist_track_pluralize_1
                }

                lastOne in 2..4 -> {
                    R.string.playlist_track_pluralize_2_4
                }


                else -> {
                    R.string.playlist_track_pluralize_last
                }
            }
        }
    }
}