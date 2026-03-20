package com.denica.playlistmaker.mediaLibrary.ui.playlist.playlistdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistInteractor
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailViewModel(
    val playlistInteractor: DbPlaylistInteractor,
    private val playlistId: Long
) : ViewModel() {
    private val playlistState = MutableLiveData<Playlist>(Playlist())
    fun getPlaylistState(): LiveData<Playlist> = playlistState
    private val playlistSongState = MutableLiveData<PlaylistSongState>()
    fun getPlaylistSongState(): LiveData<PlaylistSongState> = playlistSongState

    private val _navigateUpEvent = MutableSharedFlow<Unit>()
    val navigateUpEvent = _navigateUpEvent.asSharedFlow()

    private var tracksJob: Job? = null

    init {
        refreshPlaylistAndTracks()
    }

    private fun refreshPlaylistAndTracks() {
        viewModelScope.launch {
            val dbPlaylist = withContext(Dispatchers.IO) {
                playlistInteractor.getPlaylist(playlistId) ?: Playlist()
            }
            playlistState.postValue(dbPlaylist)
            collectTracks(dbPlaylist)
        }
    }

    private fun collectTracks(playlist: Playlist) {
        tracksJob?.cancel()

        if (playlist.trackIds.isEmpty()) {
            playlistSongState.postValue(PlaylistSongState.Empty)
            return
        }

        tracksJob = viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getPlaylistSongsByIds(playlist.trackIds).collect { songs ->
                processResult(songs, getAllTracksDuration(songs))
            }
        }
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
            playlistInteractor.deletePlaylist(playlistId)
            _navigateUpEvent.emit(Unit)
        }
    }

    fun getPlaylistToUpdateUI() {
        viewModelScope.launch {
            val dbPlaylist: Playlist
            withContext(Dispatchers.IO) {
                dbPlaylist = playlistInteractor.getPlaylist(playlistId) ?: Playlist()
            }
            playlistState.postValue(dbPlaylist)
            collectTracks(dbPlaylist)
        }
    }

    fun removeSongFromPlaylist(song: Song) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlayList(
                playlistId,
                song.trackId
            )
            withContext(Dispatchers.IO) {
                val dbPlaylist = playlistInteractor.getPlaylist(playlistId) ?: Playlist()
                playlistState.postValue(dbPlaylist)
                collectTracks(dbPlaylist)
            }
        }
    }
}
