package com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.denica.playlistmaker.databinding.FragmentPlaylistsBinding
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaLibrary.ui.MediaLibraryFragmentDirections
import com.denica.playlistmaker.search.ui.SearchFragment
import com.denica.playlistmaker.utils.BindingFragment
import com.denica.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistFragment : BindingFragment<FragmentPlaylistsBinding>() {

    val viewModel by viewModel<PlaylistViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mediaLibraryButtonAddNewPlaylist.setOnClickListener {
            findNavController().navigate(
                MediaLibraryFragmentDirections.actionMediaLibraryFragmentToCreatePlaylistFragment()
            )
        }

        val onPlaylistDebounce = debounce<Playlist>(
            SearchFragment.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            findNavController().navigate(
                MediaLibraryFragmentDirections.actionMediaLibraryFragmentToPlaylistDetailFragment(playlist)
            )
        }
        val adapter = PlaylistAdapter(onPlaylistDebounce)
        binding.playlistRecycler.adapter = adapter
        binding.playlistRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistState.Loading -> {
                    binding.playlistProgressbar.isVisible = true
                    binding.playlistPlaceholderNoPlaylist.isVisible = false
                    binding.playlistRecycler.isVisible = false
                }

                is PlaylistState.Empty -> {
                    binding.playlistProgressbar.isVisible = false
                    binding.playlistPlaceholderNoPlaylist.isVisible = true
                    binding.playlistRecycler.isVisible = false
                }

                is PlaylistState.Content -> {
                    binding.playlistProgressbar.isVisible = false
                    binding.playlistPlaceholderNoPlaylist.isVisible = false
                    binding.playlistRecycler.isVisible = true
                    adapter.itemList = it.data
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }

    override fun onStop() {
        super.onStop()

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            PlaylistFragment().apply {

            }
    }
}
