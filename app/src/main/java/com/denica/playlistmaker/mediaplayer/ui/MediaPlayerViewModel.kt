package com.denica.playlistmaker.mediaplayer.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerViewModel(private val previewUrl: String) : ViewModel() {

    private var mediaPlayer = MediaPlayer()


    private val mediaPlayerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = mediaPlayerStateLiveData

    private val mediaPlayerTimerLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = mediaPlayerTimerLiveData
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
            mediaPlayerTimerLiveData.postValue(dateFormat.format(mediaPlayer.currentPosition))
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
            mediaPlayerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayerStateLiveData.postValue(STATE_PREPARED)
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        mediaPlayerStateLiveData.postValue(STATE_PLAYING)
        startTimer()
    }

    fun pausePlayer() {
        mainHandler.removeCallbacks(createUpdateTimerTask)
        mediaPlayer.pause()
        mediaPlayerStateLiveData.postValue(STATE_PAUSED)

    }

    private fun resetTimer() {
        mainHandler.removeCallbacks(createUpdateTimerTask)
        mediaPlayerTimerLiveData.postValue("00:00")
    }

    fun onPlayButtonClicked() {
        when (mediaPlayerStateLiveData.value) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }


    companion object {
        fun getFactory(url: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MediaPlayerViewModel(url)
            }
        }

        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        private const val DELAY = 1000L
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        mainHandler.removeCallbacks(createUpdateTimerTask)
    }
}

