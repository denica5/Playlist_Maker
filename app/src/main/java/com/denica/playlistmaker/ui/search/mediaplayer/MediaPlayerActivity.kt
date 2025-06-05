package com.denica.playlistmaker.ui.search.mediaplayer

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.denica.playlistmaker.R
import com.denica.playlistmaker.ui.search.TRACK_KEY
import com.denica.playlistmaker.ui.search.TrackListViewHolder
import com.denica.playlistmaker.data.dto.SongDto
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var arrowBackMediaPlayer: ImageView
    private lateinit var trackImageMediaPlayer: ImageView
    private lateinit var trackNameMediaPlayer: TextView
    private lateinit var trackArtistNameMediaPlayer: TextView
    private lateinit var remainingTrackDurationMediaPlayer: TextView
    private lateinit var trackDurationMediaPlayer: TextView
    private lateinit var trackAlbumMediaPlayer: TextView
    private lateinit var trackYearMediaPlayer: TextView
    private lateinit var trackGenreMediaPlayer: TextView
    private lateinit var trackCountryMediaPlayer: TextView
    private lateinit var playTrackMediaPlayer: ImageView
    private lateinit var previewUrl: String
    private lateinit var mainHandler: Handler
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_media_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.media_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mainHandler = Handler(Looper.getMainLooper())
        val songDto: SongDto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_KEY, SongDto::class.java) as SongDto
        } else {
            intent.getParcelableExtra<SongDto>(TRACK_KEY) as SongDto
        }
        arrowBackMediaPlayer = findViewById(R.id.arrow_back_media_player)
        trackImageMediaPlayer = findViewById(R.id.track_image_media_player)
        trackNameMediaPlayer = findViewById(R.id.track_name_media_player)
        trackArtistNameMediaPlayer = findViewById(R.id.track_artist_name_media_player)
        remainingTrackDurationMediaPlayer = findViewById(R.id.remaining_track_duration_media_player)
        trackDurationMediaPlayer = findViewById(R.id.track_duration_media_player)
        trackAlbumMediaPlayer = findViewById(R.id.track_album_media_player)
        trackYearMediaPlayer = findViewById(R.id.track_year_media_player)
        trackGenreMediaPlayer = findViewById(R.id.track_genre_media_player)
        trackCountryMediaPlayer = findViewById(R.id.track_country_media_player)
        playTrackMediaPlayer = findViewById(R.id.play_track_media_player)
        arrowBackMediaPlayer.setOnClickListener {
            finish()
        }

        Glide.with(trackImageMediaPlayer)
            .load(songDto.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.ic_track_placeholder).centerCrop().transform(
                RoundedCorners(
                    TrackListViewHolder.dpToPx(
                        8f, trackImageMediaPlayer.context
                    )
                )
            ).into(trackImageMediaPlayer)
        trackNameMediaPlayer.text = songDto.trackName
        trackArtistNameMediaPlayer.text = songDto.artistName
        remainingTrackDurationMediaPlayer.text = dateFormat.format(0L)
        trackDurationMediaPlayer.text = dateFormat.format(songDto.trackTimeMillis)
        trackAlbumMediaPlayer.text = songDto.collectionName
        trackYearMediaPlayer.text = songDto.releaseDate?.subSequence(0, 4) ?: ""
        trackGenreMediaPlayer.text = songDto.primaryGenreName
        trackCountryMediaPlayer.text = songDto.country
        previewUrl = songDto.previewUrl?.trim() ?: ""

        if (previewUrl != "") {
            preparePlayer()
            playTrackMediaPlayer.setOnClickListener {
                playbackControl()
            }
        }

    }


    private fun startTimer() {
        mainHandler.post(
            createUpdateTimerTask
        )
    }

    private var createUpdateTimerTask = object : Runnable {
        override fun run() {
            mainHandler.postDelayed(this, DELAY)
            remainingTrackDurationMediaPlayer.text = dateFormat.format(mediaPlayer.currentPosition)
        }


    }


    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playTrackMediaPlayer.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mainHandler.removeCallbacks(createUpdateTimerTask)
            playTrackMediaPlayer.setImageResource(R.drawable.ic_play_track)
            remainingTrackDurationMediaPlayer.text = dateFormat.format(0L)
            playerState = STATE_PREPARED

        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playTrackMediaPlayer.setImageResource(R.drawable.ic_stop_track)
        playerState = STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        mainHandler.removeCallbacks(createUpdateTimerTask)

        mediaPlayer.pause()
        playTrackMediaPlayer.setImageResource(R.drawable.ic_play_track)
        playerState = STATE_PAUSED


    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                mainHandler.removeCallbacks(createUpdateTimerTask)
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 1000L
    }
}