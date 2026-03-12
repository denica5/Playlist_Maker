package com.denica.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.denica.playlistmaker.R
import com.denica.playlistmaker.main.ui.theme.MyAppTheme
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaLibrary.ui.favouriteTracks.FavouriteTracksViewModel
import com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists.PlaylistViewModel
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SearchFragment
import com.denica.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {

    val favouriteTracksViewModel by viewModel<FavouriteTracksViewModel>()
    val playlistViewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        favouriteTracksViewModel.getFavouriteSongs()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val favouriteTracksState by favouriteTracksViewModel
                    .observeFavouriteState()
                    .collectAsState()

                val playlistState by playlistViewModel
                    .observePlaylistState()
                    .collectAsState()

                val onSongClickDebounce = remember {
                    debounce<Song>(
                        SearchFragment.Companion.CLICK_DEBOUNCE_DELAY,
                        viewLifecycleOwner.lifecycleScope,
                        false
                    )
                    { song ->

                        navDebounce {
                            findNavController().navigate(
                                MediaLibraryFragmentDirections.actionMediaLibraryFragmentToMediaPlayerFragment(
                                    song
                                )
                            )
                        }

                    }
                }
                val onPlaylistDebounce = remember {
                    debounce<Playlist>(
                        SearchFragment.CLICK_DEBOUNCE_DELAY,
                        viewLifecycleOwner.lifecycleScope,
                        false
                    ) { playlist ->
                        navDebounce {
                            findNavController().navigate(
                                MediaLibraryFragmentDirections.actionMediaLibraryFragmentToPlaylistDetailFragment(
                                    playlist
                                )
                            )
                        }
                    }
                }
                val onAddPlaylistDebounce = remember {
                    debounce(
                        200L,
                        viewLifecycleOwner.lifecycleScope,
                        false
                    ) {

                        navDebounce{
                            findNavController().navigate(
                                MediaLibraryFragmentDirections.actionMediaLibraryFragmentToCreatePlaylistFragment()
                            )
                        }

                    }
                }

                MyAppTheme {

                    MediaLibraryScreen(
                        favouriteTracksState = favouriteTracksState,
                        playlistState = playlistState,
                        onSongClick = { song ->

                            navDebounce {
                                findNavController().navigate(
                                    MediaLibraryFragmentDirections.actionMediaLibraryFragmentToMediaPlayerFragment(
                                        song
                                    )
                                )
                            }

                        },
                        onPlaylistClick = onPlaylistDebounce,
                        onAddPlaylistClick = {

                            navDebounce{
                                findNavController().navigate(
                                    MediaLibraryFragmentDirections.actionMediaLibraryFragmentToCreatePlaylistFragment()
                                )
                            }

                        }

                    )
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        playlistViewModel.getPlaylists()
    }

    fun navDebounce(block: () -> Unit) {
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.mediaLibraryFragment) {
            block()
        }
    }
}