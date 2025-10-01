package com.denica.playlistmaker.mediaplayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.FragmentMediaPlayerBinding
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.TrackListViewHolder
import com.denica.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerFragment : BindingFragment<FragmentMediaPlayerBinding>() {


    private val args by navArgs<MediaPlayerFragmentArgs>()

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaPlayerBinding {
        return FragmentMediaPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val songDto: Song = args.song
        val viewModel by viewModel<MediaPlayerViewModel> {
            parametersOf(songDto)
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
        binding.arrowBackMediaPlayer.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.getPlayerState()
            .observe(viewLifecycleOwner) {
                when (it) {
                    is PlayerState.Default -> {
                        binding.playTrackMediaPlayer.setImageResource(
                            R.drawable.ic_play_track
                        )
                        binding.remainingTrackDurationMediaPlayer.text = it.progress
                    }

                    is PlayerState.Prepared -> {
                        binding.playTrackMediaPlayer.setImageResource(
                            R.drawable.ic_play_track
                        )
                        binding.remainingTrackDurationMediaPlayer.text = it.progress
                    }

                    is PlayerState.Playing -> {
                        binding.playTrackMediaPlayer.setImageResource(
                            R.drawable.ic_stop_track
                        )
                        binding.remainingTrackDurationMediaPlayer.text = it.progress
                    }

                    is PlayerState.Paused -> {
                        binding.playTrackMediaPlayer.setImageResource(
                            R.drawable.ic_play_track
                        )
                        binding.remainingTrackDurationMediaPlayer.text = it.progress
                    }

                }
            }
        binding.addToFavouriteTrackMediaPlayer.setOnClickListener {
            viewModel.onFavouriteClick(songDto)
        }
        viewModel.getFavourite().observe(viewLifecycleOwner) {
            binding.addToFavouriteTrackMediaPlayer.setImageResource(
                if (it)
                    R.drawable.ic_add_to_favourite_fill
                else
                    R.drawable.ic_add_to_favourite
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onPause() {
        super.onPause()

    }

}