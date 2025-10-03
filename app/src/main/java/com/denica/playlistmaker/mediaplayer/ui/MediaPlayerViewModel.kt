package com.denica.playlistmaker.mediaplayer.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistInteractor
import com.denica.playlistmaker.mediaLibrary.domain.DbSongInteractor
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaLibrary.ui.playlist.PlaylistState
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerViewModel(
    private val song: Song,
    private val mediaPlayer: MediaPlayer,
    private val dbSongInteractor: DbSongInteractor,
    private val playlistInteractor: DbPlaylistInteractor
) : ViewModel() {


    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun getPlayerState(): LiveData<PlayerState> = playerState
    private val playlistState: MutableLiveData<PlaylistState> = MutableLiveData(PlaylistState.Empty)
    fun observePlaylistState(): LiveData<PlaylistState> = playlistState
    private val addOrRemoveState: MutableLiveData<AddedPlaylistState> =
        MutableLiveData<AddedPlaylistState>()

    fun observeAddOrRemoveState(): LiveData<AddedPlaylistState> = addOrRemoveState
    val previewUrl = song.previewUrl.trim()

    private val isFavourite = MutableLiveData(song.isFavourite)
    fun getFavourite(): LiveData<Boolean> = isFavourite
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var timerJob: Job? = null
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(300L)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }

        }
    }


    init {
        if (previewUrl != "") {
            initMediaPlayer()
        }
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

    private fun initMediaPlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState.postValue(
                PlayerState.Prepared()
            )
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerState.postValue(PlayerState.Prepared())
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

    private fun startPlayer() {
        mediaPlayer.start()
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        playerState.value = PlayerState.Default()
    }

    fun onPlayButtonClicked() {
        when (playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return dateFormat.format(mediaPlayer.currentPosition)
            ?: "00:00"
    }

    companion object {


    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}

