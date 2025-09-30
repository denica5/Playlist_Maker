package com.denica.playlistmaker.mediaLibrary.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.denica.playlistmaker.utils.BindingFragment
import com.denica.playlistmaker.databinding.FragmentPlaylistsBinding
import com.denica.playlistmaker.mediaLibrary.ui.MediaLibraryFragmentDirections
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

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            PlaylistFragment().apply {

            }
    }
}
