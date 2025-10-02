package com.denica.playlistmaker.mediaLibrary.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistInteractor
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(val playlistInteractor: DbPlaylistInteractor) : ViewModel() {

    private val playlistState: MutableLiveData<PlaylistState> = MutableLiveData(PlaylistState.Empty)
    fun observePlaylistState(): LiveData<PlaylistState> = playlistState

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
        playlistState.postValue(state)
    }
}