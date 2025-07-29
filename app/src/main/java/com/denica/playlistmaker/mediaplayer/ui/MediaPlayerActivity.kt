package com.denica.playlistmaker.mediaplayer.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.TRACK_KEY
import com.denica.playlistmaker.search.ui.TrackListViewHolder
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {


    private lateinit var previewUrl: String
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private lateinit var binding: ActivityMediaPlayerBinding
    private lateinit var viewModel: MediaPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.media_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val songDto: Song = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_KEY, Song::class.java) as Song
        } else {
            intent.getParcelableExtra<Song>(TRACK_KEY) as Song
        }
        previewUrl = songDto.previewUrl.trim()
        viewModel = ViewModelProvider(this, MediaPlayerViewModel.getFactory(previewUrl)).get(
                MediaPlayerViewModel::class.java
            )
        binding.arrowBackMediaPlayer.setOnClickListener {
            finish()
        }

        Glide.with(binding.trackImageMediaPlayer)
            .load(songDto.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.ic_track_placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TrackListViewHolder.dpToPx(
                        8f, binding.trackImageMediaPlayer.context
                    )
                )
            )
            .into(binding.trackImageMediaPlayer)
        binding.trackNameMediaPlayer.text = songDto.trackName
        binding.trackArtistNameMediaPlayer.text = songDto.artistName
        binding.remainingTrackDurationMediaPlayer.text = dateFormat.format(0L)
        binding.trackDurationMediaPlayer.text = dateFormat.format(songDto.trackTimeMillis)
        binding.trackAlbumMediaPlayer.text = songDto.collectionName
        binding.trackYearMediaPlayer.text = songDto.releaseDate?.subSequence(0, 4) ?: ""
        binding.trackGenreMediaPlayer.text = songDto.primaryGenreName
        binding.trackCountryMediaPlayer.text = songDto.country
        binding.playTrackMediaPlayer.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        viewModel.observePlayerState()
            .observe(this) {
                setPlayTrackImage()
            }
        viewModel.observeProgressTime()
            .observe(this) {
                binding.remainingTrackDurationMediaPlayer.text = it
            }

    }


    private fun setPlayTrackImage() {
        viewModel.observePlayerState()
            .observe(this) {
                when (it) {
                    MediaPlayerViewModel.STATE_PLAYING -> binding.playTrackMediaPlayer.setImageResource(
                        R.drawable.ic_stop_track
                    )

                    MediaPlayerViewModel.STATE_PAUSED, MediaPlayerViewModel.STATE_DEFAULT, MediaPlayerViewModel.STATE_PREPARED -> binding.playTrackMediaPlayer.setImageResource(
                        R.drawable.ic_play_track
                    )
                }
            }
    }


    override fun onPause() {
        super.onPause()

    }
}