package com.denica.playlistmaker.mediaplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistInteractor
import com.denica.playlistmaker.mediaLibrary.domain.DbSongInteractor
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists.PlaylistState
import com.denica.playlistmaker.mediaplayer.musicService.IMusicService
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MediaPlayerViewModel(
    private val song: Song,
    private val dbSongInteractor: DbSongInteractor,
    private val playlistInteractor: DbPlaylistInteractor
) : ViewModel() {

    private var service: IMusicService? = null
    private val playerState = MutableStateFlow<PlayerState>(PlayerState.Default())
    fun getPlayerState(): StateFlow<PlayerState> = playerState
    private val playlistState: MutableLiveData<PlaylistState> = MutableLiveData(PlaylistState.Empty)
    fun observePlaylistState(): LiveData<PlaylistState> = playlistState
    private val addOrRemoveState: MutableLiveData<AddedPlaylistState> =
        MutableLiveData<AddedPlaylistState>()

    fun observeAddOrRemoveState(): LiveData<AddedPlaylistState> = addOrRemoveState
    val previewUrl = song.previewUrl.trim()

    private val isFavourite = MutableLiveData(song.isFavourite)
    fun getFavourite(): LiveData<Boolean> = isFavourite

    init {

        getPlaylists()
    }

    fun onFavouriteClick(song: Song) {
        viewModelScope.launch {
            if (song.isFavourite) {
                dbSongInteractor.deleteSongFromFavourite(song)
                isFavourite.postValue(!song.isFavourite)
                song.isFavourite = !song.isFavourite
            } else {
                dbSongInteractor.addSongToFavourite(song)
                isFavourite.postValue(!song.isFavourite)
                song.isFavourite = !song.isFavourite
            }
        }
    }

    fun onServiceConnected(service: IMusicService) {
        this.service = service

        viewModelScope.launch {
            service.playerState.collect {
                playerState.value = it
            }
        }
    }

    fun getPlaylists() {

        viewModelScope.launch {
            renderState(PlaylistState.Loading)
            playlistInteractor.getPlaylistList().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist, trackId: Long) {
        viewModelScope.launch {
            addOrRemoveState.postValue(
                AddedPlaylistState(
                    playlist.name,
                    playlistInteractor.addTrackToPlayList(playlist.id, trackId)
                )
            )
            playlistInteractor.addPlaylistSong(song)
            getPlaylists()
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







    fun onPlayButtonClicked() {
        when (playerState.value) {
            is PlayerState.Playing -> {
                service?.pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                service?.startPlayer()
            }

            else -> {}
        }
    }

    fun showNotification() {
        service?.showNotification()
    }
    fun hideNotification() {
        service?.hideNotification()
    }



    companion object {


    }

    override fun onCleared() {
        super.onCleared()

    }
}

