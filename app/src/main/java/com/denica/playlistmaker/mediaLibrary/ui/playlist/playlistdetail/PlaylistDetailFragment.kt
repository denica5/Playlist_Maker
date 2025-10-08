package com.denica.playlistmaker.mediaLibrary.ui.playlist.playlistdetail

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.FragmentPlaylistDetailBinding
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaplayer.ui.MediaPlayerBottomBehaviorViewHolder
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SearchFragment
import com.denica.playlistmaker.search.ui.TrackListAdapter
import com.denica.playlistmaker.search.ui.TrackListViewHolder.Companion.dpToPx
import com.denica.playlistmaker.utils.BindingFragment
import com.denica.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale


class PlaylistDetailFragment : BindingFragment<FragmentPlaylistDetailBinding>() {

    private val args by navArgs<PlaylistDetailFragmentArgs>()

    private lateinit var overflowMenuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var viewModel: PlaylistDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun createBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPlaylistDetailBinding {
        return FragmentPlaylistDetailBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistViewModel: Playlist = args.playlist
        viewModel = getViewModel<PlaylistDetailViewModel>(
            parameters = { parametersOf(playlistViewModel) }
        )

        overflowMenuBottomSheetBehavior =
            BottomSheetBehavior.from(binding.overflowMenuBottomSheetPlaylistDetail).apply {
                state =
                    BottomSheetBehavior.STATE_HIDDEN
                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN -> {
                                binding.overlay.visibility = View.GONE
                                binding.bottomSheetPlaylistDetail.isVisible = true
                            }

                            else -> {
                                binding.overlay.visibility = View.VISIBLE
                                binding.bottomSheetPlaylistDetail.isVisible = false
                            }
                        }
                    }

                    override fun onSlide(p0: View, p1: Float) {

                    }
                }
                )
            }

        val onSongClickDebounce = debounce<Song>(
            SearchFragment.Companion.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { song ->

            findNavController().navigate(
                PlaylistDetailFragmentDirections.actionPlaylistDetailFragmentToMediaPlayerFragment(
                    song
                )
            )

        }
        setupUI()


        val onSongLongClickDebounce = debounce<Song>(
            SearchFragment.Companion.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { song ->
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertTheme)
                .setTitle(getString(R.string.delete_track_from_playlist_alert_playlist_detail))
                .setNegativeButton("Нет") { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("Да") { dialog, which ->
                    viewModel.removeSongFromPlaylist(song)
                }.show()
        }
        val adapter = TrackListAdapter(onSongClickDebounce, onSongLongClickDebounce)

        binding.bottomBehaviorPlaylistsRecycle.adapter = adapter
        binding.bottomBehaviorPlaylistsRecycle.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )

        setupChangedUI(adapter)

    }


    fun shareIntent(songs: List<Song>, playlistName: String) {
        val value = StringBuilder().append(playlistName).append("\n")
            .append(
                MediaPlayerBottomBehaviorViewHolder.pluralizeTracks(songs.size, requireContext())
            ).append("\n")
        songs.forEachIndexed { index, song ->
            value.append(
                "${index + 1} ${song.artistName} - ${song.trackName} (${
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(song.trackTimeMillis).trim()
                })"
            )
            value.append("\n")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, value.toString())
            type = "text/plain"
        }
        startActivity(
            Intent.createChooser(
                intent,
                getString(R.string.share_playlist_fragment)
            )
        )
    }

    fun setImagePlaylistDetail(uri: String) {
        if (uri.startsWith("android")) {
            binding.imagePlaylistDetail.setPadding(
                requireContext().resources.getDimensionPixelSize(
                    R.dimen.image_padding
                )
            )
            setGlide(
                uri,
                binding.imagePlaylistDetail,
                MultiTransformation(CenterCrop()),
                R.drawable.ic_track_placeholder
            )
        } else {
            binding.imagePlaylistDetail.setPadding(0)
//            Glide.with(binding.imagePlaylistDetail)
//                .load(uri)
//                .placeholder(R.drawable.ic_track_placeholder)
//                .transform(CenterCrop())
//                .into(binding.imagePlaylistDetail)
            setGlide(
                uri,
                binding.imagePlaylistDetail,
                MultiTransformation(CenterCrop()),
                R.drawable.ic_track_placeholder
            )
        }
    }

    fun setupChangedUI(adapter: TrackListAdapter) {
        viewModel.getPlaylistSongState().observe(viewLifecycleOwner) { content ->
            when (content) {
                is PlaylistSongState.Content -> {
                    adapter.itemList = content.data
                    adapter.notifyDataSetChanged()
                    binding.allTracksDurationPlaylistDetail.text = getString(
                        R.string.all_tracks_duration_playlist_detail,
                        content.allTracksDuration
                    )
                    binding.tracksCountPaylistDetail.text =
                        MediaPlayerBottomBehaviorViewHolder.pluralizeTracks(
                            content.data.size,
                            requireContext()
                        )
                    binding.playlistTracksCountOverflowMenuBottomSheetPlaylistDetail.text =
                        MediaPlayerBottomBehaviorViewHolder.pluralizeTracks(
                            content.data.size,
                            requireContext()
                        )
                    viewModel.getPlaylistState().observe(viewLifecycleOwner) { playlist ->
                        binding.icSharePlaylistDetail.setOnClickListener {
                            shareIntent(content.data, playlist.name)
                        }
                        binding.sharePlaylistOverflowMenuBottomSheetPlaylistDetail.setOnClickListener {
                            shareIntent(content.data, playlist.name)
                        }
                    }
                    binding.bottomBehaviorPlaylistsRecycle.isVisible = true
                    binding.emptyTracksPlaceholderBottomSheetPlaylistDetail.isVisible = false
                }

                PlaylistSongState.Empty -> {
                    adapter.itemList = emptyList()
                    adapter.notifyDataSetChanged()
                    binding.allTracksDurationPlaylistDetail.text = getString(
                        R.string.all_tracks_duration_playlist_detail,
                        "0"
                    )
                    binding.tracksCountPaylistDetail.text =
                        getString(R.string.tracks_count_playlist_detail, 0)
                    binding.playlistTracksCountOverflowMenuBottomSheetPlaylistDetail.text =
                        getString(R.string.tracks_count_playlist_detail, 0)
                    binding.icSharePlaylistDetail.setOnClickListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.playlist_dont_have_tracks_playlist_detail),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    binding.sharePlaylistOverflowMenuBottomSheetPlaylistDetail.setOnClickListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.playlist_dont_have_tracks_playlist_detail),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.bottomBehaviorPlaylistsRecycle.isVisible = false
                    binding.emptyTracksPlaceholderBottomSheetPlaylistDetail.isVisible = true
                }

                PlaylistSongState.Loading -> {
                    binding.allTracksDurationPlaylistDetail.text = getString(
                        R.string.all_tracks_duration_playlist_detail,
                        "0"
                    )
                    binding.tracksCountPaylistDetail.text =
                        getString(R.string.tracks_count_playlist_detail, 0)
                }
            }
        }
    }

    fun setupUI() {
        binding.icOverflowMenuPlaylistDetail.setOnClickListener {
            overflowMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.arrowBackPlaylistDetail.setOnClickListener {
            findNavController().navigateUp()
        }
        viewModel.getPlaylistState().observe(viewLifecycleOwner) { playlist ->
            setGlide(
                playlist.imagePath, binding.imageOverflowMenuBottomSheetPlaylistDetail,
                MultiTransformation(
                    CenterCrop(),
                    RoundedCorners(
                        dpToPx(
                            2f,
                            binding.imageOverflowMenuBottomSheetPlaylistDetail.context
                        )
                    )
                ),
                R.drawable.ic_track_placeholder
            )

            binding.playlistNameOverflowMenuBottomSheetPlaylistDetail.text = playlist.name
            setImagePlaylistDetail(uri = playlist.imagePath)
            binding.namePlaylistDetail.text = playlist.name
            binding.descriptionPlaylistDetail.text = playlist.description
            binding.deletePlaylistOverflowMenuBottomSheetPlaylistDetail.setOnClickListener {
                overflowMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                MaterialAlertDialogBuilder(requireContext(), R.style.AlertTheme)
                    .setTitle(
                        getString(
                            R.string.delete_playlist_alert_playlist_detail,
                            playlist.name
                        )
                    )
                    .setNegativeButton(getString(R.string.negative_button_alert_playlist_detail)) { dialog, which ->
                        dialog.cancel()
                    }
                    .setPositiveButton(getString(R.string.positive_button_alert_playlist_detail)) { dialog, which ->
                        viewModel.deletePlaylist()
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.navigateUpEvent.collect {
                                findNavController().navigateUp()
                            }
                        }
                    }.show()
            }
            binding.editPlaylistOverflowMenuBottomSheetPlaylistDetail.setOnClickListener {
                findNavController().navigate(
                    PlaylistDetailFragmentDirections.actionPlaylistDetailFragmentToEditPlaylistFragment(
                        playlist
                    )
                )
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylistToUpdateUI()
        overflowMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onPause() {
        super.onPause()
        overflowMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

    }

    companion object {

        @JvmStatic
        fun newInstance() = PlaylistDetailFragment().apply {

        }

        fun setGlide(
            uri: String,
            bindingImage: ImageView,
            transformation: MultiTransformation<Bitmap>,
            placeHolder: Int
        ) {

            Glide.with(bindingImage)
                .load(uri)
                .placeholder(placeHolder)
                .transform(transformation)
                .apply(
                    RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(
                            true
                        )
                )
                .into(bindingImage)
        }
    }

}