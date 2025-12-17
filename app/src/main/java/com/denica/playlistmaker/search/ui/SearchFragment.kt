package com.denica.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil3.compose.AsyncImage
import com.denica.playlistmaker.LocalAppColors
import com.denica.playlistmaker.MyAppTheme
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.FragmentSearchBinding
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SearchFragment.Companion.formatDuration
import com.denica.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale
import java.util.concurrent.TimeUnit


class SearchFragment : Fragment() {
    private var searchText = ""
    val viewModel by viewModel<SearchViewModel>()
    private lateinit var adapter: TrackListAdapter
    private lateinit var historyAdapter: TrackListAdapter

    private lateinit var onSearchSongClickDebounce: (Song) -> Unit
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!


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
                    onSearchSongClickDebounce =
                        debounce<Song>(
                            CLICK_DEBOUNCE_DELAY,
                            viewLifecycleOwner.lifecycleScope,
                            false
                        )
                        { song ->
                            var position = -1
                            viewLifecycleOwner.lifecycleScope.launch {
                                position = viewModel.addTrack(song)
                            }

                            findNavController().navigate(
                                SearchFragmentDirections.actionSearchFragment2ToMediaPlayerFragment(
                                    song
                                )
                            )
                            if (position != -1) {
                                historyAdapter.notifyItemMoved(position, 0)
//                                binding.trackListRc.scrollToPosition(0)
                            }

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

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        _binding = FragmentSearchBinding.bind(view)
//

//
//
//        adapter = TrackListAdapter(onSearchSongClickDebounce)
//        historyAdapter = TrackListAdapter(onSearchSongClickDebounce)
//
//        viewModel.getSearchHistoryState()
//            .observe(viewLifecycleOwner) {
//                when (it) {
//                    is SearchHistoryState.Content -> historyAdapter.itemList = it.data
//                    is SearchHistoryState.Empty -> {
//                        historyAdapter.itemList = it.data
//                    }
//                }
//            }
//        binding.searchEditText.setText(searchText)
//
//        binding.searchClearIcX.setOnClickListener {
//            viewModel.removeSearchList()
//            binding.searchEditText.setText("")
//            try {
//                val imm =
//                    requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(it.windowToken, 0)
//                clearTracks("clear_button")
//            } catch (_: Exception) {
//                //
//            }
//
//        }
//        binding.refreshButton.setOnClickListener {
//
//            debounce<Button>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) {
//                viewModel.searchRequest(
//                    binding.searchEditText.text.toString()
//                )
//            }
//
//
//        }
//
//
//        val searchTextWatcher = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                //
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                binding.searchClearIcX.isVisible = !s.isNullOrEmpty()
//                searchText = s.toString()
//                if (searchText != "") {
//                    viewModel.searchDebounce(searchText)
//                }
//                if (viewModel.isSavedTracksArrayListNotEmpty()) {
//                    if (binding.searchEditText.hasFocus() && s?.isEmpty() == true) {
//                        binding.clearHistoryButton.isVisible = true
//                        binding.youSearchTextView.isVisible = true
//                        binding.trackListRc.adapter = historyAdapter
//                    } else {
//                        binding.clearHistoryButton.isVisible = false
//                        binding.youSearchTextView.isVisible = false
//                        binding.trackListRc.adapter = adapter
//                    }
//                } else {
//                    binding.clearHistoryButton.isVisible = false
//                    binding.youSearchTextView.isVisible = false
//                    binding.trackListRc.adapter = adapter
//                }
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                //
//            }
//        }
//        binding.searchEditText.addTextChangedListener(searchTextWatcher)
//        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->
//            showHistory()
//        }
//        binding.trackListRc.adapter = adapter
//        binding.trackListRc.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                if (binding.searchEditText.text.isNotEmpty()) {
//                    viewModel.searchRequest(binding.searchEditText.text.toString())
//                } else {
//                    clearTracks(getString(R.string.nothing_found))
//                }
//                return@setOnEditorActionListener true
//            }
//            false
//        }
//
//        binding.clearHistoryButton.setOnClickListener {
//            viewModel.clearHistory()
//            binding.clearHistoryButton.isVisible = false
//            binding.youSearchTextView.isVisible = false
//
//        }
//        viewModel.getState()
//            .observe(viewLifecycleOwner) {
//                when (it) {
//                    is SearchState.Loading -> {
//                        binding.searchProgressBar.isVisible = true
//                        binding.trackListRc.isVisible = false
//                        binding.notFoundError.isVisible = false
//                        binding.failedSearchError.isVisible = false
//                    }
//
//                    is SearchState.Content -> {
//                        adapter.itemList = it.data
//                        adapter.notifyDataSetChanged()
//                        clearTracks("")
//                    }
//
//                    is SearchState.Empty -> {
//                        clearTracks(getString(it.message))
//                    }
//
//                    is SearchState.Error -> {
//                        clearTracks(getString(it.message))
//                    }
//                }
//            }
//    }


//    private fun clearTracks(text: String) {
//
//        if (text.isNotEmpty()) {
//            adapter.itemList = emptyList()
//            adapter.notifyDataSetChanged()
//        }
//        when (text) {
//            getString(R.string.failed_search) -> {
//                binding.notFoundError.isVisible = false
//                binding.trackListRc.isVisible = false
//                binding.failedSearchError.isVisible = true
//                binding.searchProgressBar.isVisible = false
//            }
//
//            getString(R.string.nothing_found) -> {
//                binding.notFoundError.isVisible = true
//                binding.trackListRc.isVisible = false
//                binding.failedSearchError.isVisible = false
//                binding.searchProgressBar.isVisible = false
//            }
//
//            "" -> {
//                binding.notFoundError.isVisible = false
//                binding.trackListRc.isVisible = true
//                binding.failedSearchError.isVisible = false
//                binding.searchProgressBar.isVisible = false
//            }
//
//            "clear_button" -> {
//                binding.notFoundError.isVisible = false
//                binding.trackListRc.isVisible = true
//                binding.failedSearchError.isVisible = false
//                binding.searchProgressBar.isVisible = false
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun showHistory() {
//        if (viewModel.isSavedTracksArrayListNotEmpty()) {
//            if (binding.searchEditText.hasFocus() && binding.searchEditText.text.isEmpty()) {
//                binding.clearHistoryButton.isVisible = true
//                binding.youSearchTextView.isVisible = true
//                binding.trackListRc.adapter = historyAdapter
//            } else {
//                binding.clearHistoryButton.isVisible = false
//                binding.youSearchTextView.isVisible = false
//                binding.trackListRc.adapter = adapter
//            }
//        }
//    }


    override fun onStop() {
        super.onStop()

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
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    //    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY) ?: ""
//    }
    @Composable
    fun SearchScreen(
        uiState: SearchState,
        onQueryChange: (String) -> Unit,
        onFocusChanged: (Boolean) -> Unit,
        onClearQuery: () -> Unit,
        onRefreshClick: () -> Unit,
        onClearHistoryClick: () -> Unit,
        textFieldState: TextFieldState,
        searchHistoryState: SearchHistoryState,
        onSearchSongClickDebounce: (Song) -> Unit,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.search_text_header),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    elevation = 0.dp
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = MaterialTheme.colorScheme.primary)
            ) {

                SearchInput(
                    query = textFieldState.query,
                    showClear = textFieldState.isShowClearIc,
                    onQueryChange = onQueryChange,
                    onClearQuery = onClearQuery,
                    onFocusChanged = onFocusChanged,
                    searchHistoryState = searchHistoryState,
                    showHistoryTitle = textFieldState.isShowHistory,
                    onClearHistoryClick = onClearHistoryClick
                )

                when (uiState) {
                    is SearchState.Loading -> {
                        Loading()
                    }

                    is SearchState.Error -> {
                        FailedSearch(
                            onRefreshClick = onRefreshClick
                        )
                    }

                    is SearchState.Empty -> {
                        NothingFound(R.string.nothing_found)
                    }

                    is SearchState.Content -> {
                        TrackListColumn(
                            tracks = uiState.data,
                            onSearchSongClickDebounce = onSearchSongClickDebounce,
                        )
                    }

                    is SearchState.Nothing -> {

                    }
                }
            }
        }
    }

    @Composable
    private fun SearchInput(
        query: String,
        showClear: Boolean,
        showHistoryTitle: Boolean,
        searchHistoryState: SearchHistoryState,
        onClearHistoryClick: () -> Unit,
        onQueryChange: (String) -> Unit,
        onFocusChanged: (Boolean) -> Unit,
        onClearQuery: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Box(

            ) {

                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            onFocusChanged(focusState.isFocused)
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_hint_edit_text),
                            color = LocalAppColors.current.editTextHint,

                            )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search_search_layout),
                            contentDescription = null,
                            tint = LocalAppColors.current.icSearchIcon,
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = LocalAppColors.current.editTextBackground,
                        focusedContainerColor = LocalAppColors.current.editTextBackground,
                        cursorColor = LocalAppColors.current.editTextCursor,
                        selectionColors = TextSelectionColors(
                            LocalAppColors.current.editTextCursor,
                            LocalAppColors.current.editTextCursorBackground
                        )

                    ),
                    textStyle = TextStyle(
                        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                        fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        color = LocalAppColors.current.editTextText
                    ),

                    )

                if (showClear) {
                    IconButton(
                        onClick = onClearQuery,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cancel_x),
                            contentDescription = null,
                            tint = LocalAppColors.current.icClearSearch

                        )
                    }
                }
            }

            if (showHistoryTitle) {
                when (searchHistoryState) {
                    is SearchHistoryState.Empty -> {

                    }

                    is SearchHistoryState.Content -> {
                        Text(
                            text = stringResource(R.string.you_search),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 24.dp, bottom = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        TrackLazyColumn(
                            searchHistoryState.data, onSearchSongClickDebounce, Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                        Button(
                            onClick = onClearHistoryClick,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.clear_history),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    private fun FailedSearch(
        onRefreshClick: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_failed_search),
                contentDescription = null,
                modifier = Modifier.padding(top = 102.dp)
            )

            Text(
                text = stringResource(R.string.failed_search),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 19.sp,

                )

            Button(
                onClick = onRefreshClick,
                modifier = Modifier.padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.button_refresh),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                )
            }
        }
    }


    @Composable
    @Preview
    private fun SearchScreenPreview() {
        MyAppTheme {
            SearchScreen(
                uiState = SearchState.Content(
                    listOf(
                        Song(
                            1,
                            "dsa",
                            "5tgfsd",
                            132313,
                            "https://img10.reactor.cc/pics/post/Shiratori-Aira-%28Dandadan%29-Dandadan-Anime-Oppai-9188368.png",
                            "3321321321",
                            null,
                            "dddddd",
                            "Amr",
                            "http://webaudioapi.com/samples/audio-tag/chrono.mp3",
                            false
                        ),
                        Song(
                            1,
                            "dsa",
                            "5tgfsddasdasdasdsadsadasdasdsadsadsadsaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                            1321,
                            "https://static.wikia.nocookie.net/dota2_gamepedia/images/d/d6/Dawnbreaker_icon.png/revision/latest/smart/width/250/height/250?cb=20210410124439",
                            "dsa31ffgppgf",
                            null,
                            "dddddd",
                            "Amr",
                            "http://webaudioapi.com/samples/audio-tag/chrono.mp3",
                            false
                        )
                    ),
                ),

                {},
                {},
                {},
                {},
                {},
                TextFieldState(),
                SearchHistoryState.Empty(),
                {}
            )

        }
    }
}

@Composable
public fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 140.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        CircularProgressIndicator(
            color = colorResource(R.color.blue),
            modifier = Modifier.size(44.dp)
        )
    }
}

@Composable
fun TrackListColumn(
    tracks: List<Song>,
    onSearchSongClickDebounce: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TrackLazyColumn(
            tracks, onSearchSongClickDebounce, modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun TrackLazyColumn(
    tracks: List<Song>,
    onSearchSongClickDebounce: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        items(tracks) { track ->
            TrackItem(track, Modifier.clickable {
                onSearchSongClickDebounce(track)
            })
        }
    }
}

@Composable
fun TrackItem(
    song: Song,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        AsyncImage(
            model = song.artworkUrl100,
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop,

            placeholder = painterResource(R.drawable.ic_track_placeholder),
            error = painterResource(R.drawable.ic_track_placeholder)
        )


        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 14.dp)
        ) {


            Text(
                text = song.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(R.color.track_item_text_color)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = song.artistName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.artist_name_and_time_color),
                    modifier = Modifier.widthIn(max = 200.dp),
                    fontSize = 11.sp
                )

                Icon(
                    painter = painterResource(R.drawable.dotsvg),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 6.dp)
                        .size(6.dp),
                    tint = colorResource(R.color.artist_name_and_time_color),
                )

                Text(
                    text = formatDuration(song.trackTimeMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.artist_name_and_time_color),
                    modifier = modifier.wrapContentWidth(),
                    fontSize = 11.sp


                )
            }
        }


        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .size(14.dp),
            tint = colorResource(R.color.artist_name_and_time_color),

            )
    }
}

@Composable
fun NothingFound(@StringRes nothingFoundText: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_not_found),
            contentDescription = null,
            modifier = Modifier.padding(top = 102.dp)
        )

        Text(
            text = stringResource(nothingFoundText),
            modifier = Modifier.padding(top = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 19.sp,

            )
    }
}