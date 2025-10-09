package com.denica.playlistmaker.mediaLibrary.ui.playlist.playlistdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistInteractor
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailViewModel(
    val playlistInteractor: DbPlaylistInteractor,
    val playlist: Playlist
) : ViewModel() {
    private val playlistState = MutableLiveData<Playlist>(playlist)
    fun getPlaylistState(): LiveData<Playlist> = playlistState
    private val playlistSongState = MutableLiveData<PlaylistSongState>()
    fun getPlaylistSongState(): LiveData<PlaylistSongState> = playlistSongState

    private val _navigateUpEvent = MutableSharedFlow<Unit>()
    val navigateUpEvent = _navigateUpEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            getAllTracks()
        }
    }

    suspend fun getAllTracks() =
        playlistInteractor.getPlaylistSongsByIds(playlist.trackIds).collect {
            processResult(it, getAllTracksDuration(it))
        }


    fun getAllTracksDuration(listSongs: List<Song>): String {

        return SimpleDateFormat("mm", Locale.getDefault()).format(listSongs.sumOf {
            it.trackTimeMillis
        })
    }

    private fun processResult(playlistSongs: List<Song>, allTracksDuration: String) {
        if (playlistSongs.isEmpty()) {
            renderState(PlaylistSongState.Empty)
        } else {
            renderState(PlaylistSongState.Content(playlistSongs, allTracksDuration))
        }
    }

    fun renderState(state: PlaylistSongState) {
        playlistSongState.postValue(state)
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist.id)
            _navigateUpEvent.emit(Unit)
        }
    }

    fun getPlaylistToUpdateUI() {
        viewModelScope.launch { playlistState.postValue(playlistInteractor.getPlaylist(playlist.id)) }
    }

    fun removeSongFromPlaylist(song: Song) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlayList(
                playlist.id,
                song.trackId
            )
            getAllTracks()
        }
    }
}