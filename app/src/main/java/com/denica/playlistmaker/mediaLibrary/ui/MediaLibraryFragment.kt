package com.denica.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.denica.playlistmaker.MyAppTheme
import com.denica.playlistmaker.R
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaLibrary.ui.favouriteTracks.FavouriteTracksScreen
import com.denica.playlistmaker.mediaLibrary.ui.favouriteTracks.FavouriteTracksState
import com.denica.playlistmaker.mediaLibrary.ui.favouriteTracks.FavouriteTracksViewModel
import com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists.PlaylistState
import com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists.PlaylistViewModel
import com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists.PlaylistsScreen
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SearchFragment
import com.denica.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
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

                val onSongClickDebounce = debounce<Song>(
                    SearchFragment.Companion.CLICK_DEBOUNCE_DELAY,
                    viewLifecycleOwner.lifecycleScope,
                    false
                ) { song ->

                    findNavController().navigate(
                        MediaLibraryFragmentDirections.actionMediaLibraryFragmentToMediaPlayerFragment(
                            song
                        )
                    )

                }
                val onPlaylistDebounce = debounce<Playlist>(
                    SearchFragment.CLICK_DEBOUNCE_DELAY,
                    viewLifecycleOwner.lifecycleScope,
                    false
                ) { playlist ->
                    findNavController().navigate(
                        MediaLibraryFragmentDirections.actionMediaLibraryFragmentToPlaylistDetailFragment(
                            playlist
                        )
                    )
                }
                val onAddPlaylistDebounce = debounce(
                    SearchFragment.CLICK_DEBOUNCE_DELAY,
                    viewLifecycleOwner.lifecycleScope,
                    false
                ) {
                    findNavController().navigate(
                        MediaLibraryFragmentDirections.actionMediaLibraryFragmentToCreatePlaylistFragment()
                    )
                }

                MyAppTheme {

                    MediaLibraryScreen(
                        favouriteTracksState = favouriteTracksState,
                        playlistState = playlistState,
                        onSongClick = onSongClickDebounce,
                        onPlaylistClick = onPlaylistDebounce,
                        onAddPlaylistClick = onAddPlaylistDebounce

                    )
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        playlistViewModel.getPlaylists()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MediaLibraryScreen(
        favouriteTracksState: FavouriteTracksState,
        playlistState: PlaylistState,
        onSongClick: (Song) -> Unit,
        onPlaylistClick: (Playlist) -> Unit,
        onAddPlaylistClick: () -> Unit
    ) {
        val tabs = listOf(
            stringResource(R.string.media_library_favourite_tracks_label),
            stringResource(R.string.media_library_playlists_label)
        )

        val pagerState = rememberPagerState(pageCount = { tabs.size })
        val coroutineScope = rememberCoroutineScope()


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.media_library_text_header),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                elevation = 0.dp,
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colorScheme.onSecondary,
                        width = 148.dp,
                        height = 2.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> FavouriteTracksScreen(
                        favouriteTracksState = favouriteTracksState,
                        onSongClick
                    )

                    1 -> PlaylistsScreen(
                        playlistState = playlistState,
                        onPlaylistClick,
                        onAddPlaylistClick
                    )
                }
            }
        }
    }
}