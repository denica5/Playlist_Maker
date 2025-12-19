package com.denica.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.denica.playlistmaker.main.ui.theme.MyAppTheme
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import java.util.concurrent.TimeUnit


class SearchFragment : Fragment() {

    val viewModel by viewModel<SearchViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                MyAppTheme {
                    val onSearchSongClickDebounce =
                        debounce<Song>(
                            CLICK_DEBOUNCE_DELAY,
                            viewLifecycleOwner.lifecycleScope,
                            false
                        )
                        { song ->
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.addTrack(song)
                            }

                            findNavController().navigate(
                                SearchFragmentDirections.actionSearchFragment2ToMediaPlayerFragment(
                                    song
                                )
                            )

                        }
                    val searchState by viewModel
                        .getSearchState()
                        .collectAsState()

                    val textFieldState by viewModel
                        .textFieldState
                        .collectAsState()
                    val searchHistoryState by viewModel
                        .getSearchHistoryState()
                        .collectAsState()
                    SearchScreen(
                        searchState,
                        viewModel::onQueryChange,
                        viewModel::onSearchFocusChanged,
                        viewModel::onClearIcClick,
                        viewModel::searchRequest,
                        viewModel::clearHistory,
                        textFieldState,
                        searchHistoryState,
                        onSearchSongClickDebounce
                    )

                }
            }
        }
    }


    override fun onResume() {
        viewModel.getHistory()
        super.onResume()

    }

    companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        const val CLICK_DEBOUNCE_DELAY = 1000L
        fun formatDuration(durationMillis: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, viewModel.textFieldState.value.query)
    }


}






