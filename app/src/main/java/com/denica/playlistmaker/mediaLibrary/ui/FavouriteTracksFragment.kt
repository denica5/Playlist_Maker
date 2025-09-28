package com.denica.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.denica.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SearchFragment
import com.denica.playlistmaker.search.ui.TrackListAdapter
import com.denica.playlistmaker.utils.BindingFragment
import com.denica.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavouriteTracksFragment : BindingFragment<FragmentFavouriteTracksBinding>() {

    val viewModel by viewModel<FavouriteTracksViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouriteTracksBinding {
        return FragmentFavouriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavouriteSongs()
        val onSongClickDebounce = debounce<Song>(
            SearchFragment.CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { song ->
            Log.d("Fragment", "${findNavController().currentDestination?.id}")
            findNavController().navigate(
                MediaLibraryFragmentDirections.actionMediaLibraryFragmentToMediaPlayerFragment(
                    song
                )
            )

        }
        val adapter = TrackListAdapter(
            onSongClickDebounce
        )
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        viewModel.observeFavouriteState().observe(viewLifecycleOwner) {
            when (it) {
                is FavouriteTracksState.Loading -> {
                    binding.favouriteProgressBar.isVisible = true
                    binding.placeholder.isVisible = false
                    binding.recycler.isVisible = false
                }

                is FavouriteTracksState.Empty -> {
                    binding.favouriteProgressBar.isVisible = false
                    binding.placeholder.isVisible = true
                    binding.recycler.isVisible = false
                }

                is FavouriteTracksState.Content -> {
                    binding.favouriteProgressBar.isVisible = false
                    binding.placeholder.isVisible = false
                    binding.recycler.isVisible = true
                    adapter.itemList = it.data
                    adapter.notifyDataSetChanged()
                }
            }
        }





    }

    companion object {

        @JvmStatic
        fun newInstance() =
            FavouriteTracksFragment().apply {
                arguments = Bundle().apply {

                }
            }

    }
}