package com.denica.playlistmaker

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_media_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.media_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val track: Track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_KEY, Track::class.java) as Track
        } else {
            intent.getParcelableExtra<Track>(TRACK_KEY) as Track
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

        arrowBackMediaPlayer.setOnClickListener {
            finish()
        }

        Glide.with(trackImageMediaPlayer)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.ic_track_placeholder).centerCrop()
            .transform(
                RoundedCorners(
                    TrackListViewHolder.dpToPx(
                        8f,
                        trackImageMediaPlayer.context
                    )
                )
            )
            .into(trackImageMediaPlayer)
        trackNameMediaPlayer.text = track.trackName
        trackArtistNameMediaPlayer.text = track.artistName
        remainingTrackDurationMediaPlayer.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        trackDurationMediaPlayer.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        trackAlbumMediaPlayer.text = track.collectionName
        trackYearMediaPlayer.text = track.releaseDate.subSequence(0,4)
        trackGenreMediaPlayer.text = track.primaryGenreName
        trackCountryMediaPlayer.text = track.country

    }
}