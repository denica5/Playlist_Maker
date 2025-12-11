package com.denica.playlistmaker.mediaplayer.musicService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.denica.playlistmaker.R
import com.denica.playlistmaker.mediaplayer.ui.MediaPlayerFragment
import com.denica.playlistmaker.mediaplayer.ui.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MusicService : Service(), IMusicService {



    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private  var previewUrl = ""
    private var trackName = ""
    private var artistName = ""

    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicServiceBinder()
    private var timerJob: Job? = null


    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default())
    override val playerState = _playerState.asStateFlow()
    override fun onBind(intent: Intent?): IBinder? {
        previewUrl = intent?.getStringExtra(MediaPlayerFragment.PREVIEW_SONG_URL_EXTRA) ?: ""
        trackName = intent?.getStringExtra(MediaPlayerFragment.TRACK_NAME_EXTRA) ?: ""
        artistName = intent?.getStringExtra(MediaPlayerFragment.ARTIST_NAME_EXTRA) ?: ""
        initMediaPlayer()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {

        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }


    override fun showNotification() {
        ServiceCompat.startForeground(
            this, SERVICE_NOTIFICATION_ID, createServiceNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(REMAINING_TIMER_DELAY)
                _playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
            }

        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerState.Paused(getCurrentPlayerPosition())
    }

    private fun releasePlayer() {
        _playerState.value = PlayerState.Default()
        timerJob?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()

    }

    private fun initMediaPlayer() {
        releasePlayer()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            setOnPreparedListener {
                _playerState.value = PlayerState.Prepared()
            }
            setOnCompletionListener {
                timerJob?.cancel()
                _playerState.value = PlayerState.Prepared()
            }
            prepareAsync()
        }
    }

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"

        // Регистрируем канал уведомлений
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()

    }

    private fun getCurrentPlayerPosition(): String {
        return dateFormat.format(mediaPlayer?.currentPosition) ?: "00:00"
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): IMusicService = this@MusicService
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$artistName - $trackName")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
        const val SERVICE_NOTIFICATION_ID = 100
        const val REMAINING_TIMER_DELAY = 300L

    }
}