package com.denica.playlistmaker.mediaplayer.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerViewModel(private val previewUrl: String) : ViewModel() {

    private var mediaPlayer = MediaPlayer()



    private val mediaPlayerState = MutableLiveData(MediaPlayerState(STATE_DEFAULT, "00:00"))
    fun getMediaPlayerState(): LiveData<MediaPlayerState> = mediaPlayerState
    private val mainHandler = Handler(Looper.getMainLooper())
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private fun startTimer() {
        mainHandler.post(
            createUpdateTimerTask
        )
    }

    private val createUpdateTimerTask = object : Runnable {
        override fun run() {
            mainHandler.postDelayed(this, 200)
            mediaPlayerState.postValue(mediaPlayerState.value?.copy(countTimer = dateFormat.format(mediaPlayer.currentPosition)))
        }


    }

    init {
        if (previewUrl != "") {
            preparePlayer()
        }

    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayerState.postValue(mediaPlayerState.value?.copy(playerState = STATE_PREPARED, countTimer = "00:00"))
        }
        mediaPlayer.setOnCompletionListener {
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        mediaPlayerState.postValue(mediaPlayerState.value?.copy(playerState = STATE_PLAYING))
        startTimer()
    }

    private fun pausePlayer() {
        mainHandler.removeCallbacks(createUpdateTimerTask)
        mediaPlayer.pause()
        mediaPlayerState.postValue(mediaPlayerState.value?.copy(playerState = STATE_PAUSED))
    }

    private fun resetTimer() {
        mainHandler.removeCallbacks(createUpdateTimerTask)
        mediaPlayerState.postValue(mediaPlayerState.value?.copy(playerState = STATE_PREPARED, countTimer = "00:00"))

    }

    fun onPlayButtonClicked() {
        when (mediaPlayerState.value?.playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }


    companion object {


        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        mainHandler.removeCallbacks(createUpdateTimerTask)
    }
}

