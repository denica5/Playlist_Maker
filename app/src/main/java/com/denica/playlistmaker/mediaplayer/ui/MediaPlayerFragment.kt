package com.denica.playlistmaker.mediaplayer.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.FragmentMediaPlayerBinding
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists.PlaylistState
import com.denica.playlistmaker.mediaplayer.musicService.IMusicService
import com.denica.playlistmaker.mediaplayer.musicService.MusicService
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SearchFragment
import com.denica.playlistmaker.search.ui.TrackListViewHolder
import com.denica.playlistmaker.utils.BindingFragment
import com.denica.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerFragment : BindingFragment<FragmentMediaPlayerBinding>() {


    private val args by navArgs<MediaPlayerFragmentArgs>()
    val viewModel by viewModel<MediaPlayerViewModel> {
        parametersOf(args.song)
    }
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var musicService: IMusicService? = null

    companion object {
        const val PREVIEW_SONG_URL_EXTRA = "song_url"
        const val ARTIST_NAME_EXTRA = "artist_name"
        const val TRACK_NAME_EXTRA = "track_name"

    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {

            Toast.makeText(requireContext(), "Can't start foreground service!", Toast.LENGTH_LONG)
                .show()
        }
    }
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicServiceBinder
            musicService = binder.getService()
            viewModel.onServiceConnected(musicService!!)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            musicService = null
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaPlayerBinding {
        return FragmentMediaPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val songDto: Song = args.song


        bottomSheetBehavior =
            BottomSheetBehavior.from(binding.playlistsBottomSheetMediaPlayer).apply {
                state =
                    BottomSheetBehavior.STATE_HIDDEN
                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN -> {
                                binding.overlay.visibility = View.GONE
                            }

                            else -> {
                                binding.overlay.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onSlide(p0: View, p1: Float) {

                    }
                }
                )
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
            ).apply(
                RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(
                        true
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
        binding.addToPlaylistMediaPlayer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.playTrackMediaPlayer.onToggle = {
            viewModel.onPlayButtonClicked()
            showForegroundNotificationIfNeeded()
        }
        binding.arrowBackMediaPlayer.setOnClickListener {
            findNavController().navigateUp()
        }
        bindMusicService(songDto)
        lifecycleScope.launch {
            viewModel.getPlayerState().collect {
                when (it) {
                    is PlayerState.Default -> {

                        binding.remainingTrackDurationMediaPlayer.text = it.progress
                    }

                    is PlayerState.Prepared -> {
                        binding.remainingTrackDurationMediaPlayer.text = it.progress
                        binding.playTrackMediaPlayer.setPlayState(false)

                    }

                    is PlayerState.Playing -> {

                        binding.remainingTrackDurationMediaPlayer.text = it.progress
                    }

                    is PlayerState.Paused -> {

                        binding.remainingTrackDurationMediaPlayer.text = it.progress
                    }

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
        viewModel.observeAddOrRemoveState()
            .observe(viewLifecycleOwner) {

                toastPlaylistAddOrRemove(it.playlistName, it.addResult)


            }
        val onPlaylistDebounce = debounce<Playlist>(
            SearchFragment.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            viewModel.addTrackToPlaylist(playlist, songDto.trackId)

        }
        val adapter = MediaPlayerBottomBehaviorAdapter(onPlaylistDebounce)

        binding.bottomBehaviorPlaylistsRecycle.adapter = adapter
        binding.bottomBehaviorPlaylistsRecycle.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistState.Loading -> {

                    binding.bottomBehaviorPlaylistsRecycle.isVisible = false
                }

                is PlaylistState.Empty -> {

                    binding.bottomBehaviorPlaylistsRecycle.isVisible = false

                }

                is PlaylistState.Content -> {

                    binding.bottomBehaviorPlaylistsRecycle.isVisible = true
                    adapter.itemList = it.data
                    adapter.notifyDataSetChanged()
                }
            }
        }
        binding.bottomBehaviorAddPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(MediaPlayerFragmentDirections.actionMediaPlayerFragmentToCreatePlaylistFragment())

        }

    }

    private fun toastPlaylistAddOrRemove(playlistName: String, result: Int) {
        val text =
            when (result) {
                0 -> {
                    getString(R.string.media_player_track_already_in_playlist, playlistName)

                }

                1 -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    getString(R.string.media_player_track_added_to_playlist, playlistName)
                }

                else -> {
                    getString(R.string.media_player_error_to_interact_with_playlist)
                }
            }
        Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_SHORT
        ).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {

        super.onResume()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onPause() {

        super.onPause()

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onStop() {

        super.onStop()
        if (viewModel.getPlayerState().value is PlayerState.Playing && requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.showNotification()
        }

    }

    override fun onStart() {
        super.onStart()
            musicService?.hideNotification()


    }

    override fun onDestroyView() {
        unbindMusicService()
        super.onDestroyView()

    }

    private fun showForegroundNotificationIfNeeded() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted =
                requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

    }

    private fun bindMusicService(songDto: Song) {
        Intent(requireContext(), MusicService::class.java).also { intent ->
            intent.putExtra(PREVIEW_SONG_URL_EXTRA, songDto.previewUrl)
            intent.putExtra(ARTIST_NAME_EXTRA, songDto.artistName)
            intent.putExtra(TRACK_NAME_EXTRA, songDto.trackName)
            requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        }
    }

    private fun unbindMusicService() {
        requireContext().unbindService(serviceConnection)
    }

}