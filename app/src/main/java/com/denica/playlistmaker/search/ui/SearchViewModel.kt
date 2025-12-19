package com.denica.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.R
import com.denica.playlistmaker.mediaLibrary.domain.DbSongInteractor
import com.denica.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.denica.playlistmaker.search.domain.api.SongInteractor
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SearchHistoryState
import com.denica.playlistmaker.utils.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val historyInteractor: SearchHistoryInteractor,
    private val songInteractor: SongInteractor,
    private val dbSongInteractor: DbSongInteractor
) : ViewModel() {

    var ids = emptyList<Long>()
    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null
    private val searchHistoryState =
        MutableStateFlow<SearchHistoryState>(SearchHistoryState.Empty())

    fun getSearchHistoryState() = searchHistoryState.asStateFlow()
    private val searchState = MutableStateFlow<SearchState>(SearchState.Nothing)
    fun getSearchState() = searchState.asStateFlow()

    private val _textFieldState = MutableStateFlow(TextFieldState())
    val textFieldState = _textFieldState.asStateFlow()
    private val songSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest()
        }

    fun onSearchFocusChanged(hasFocus: Boolean) {
        _textFieldState.update {
            it.copy(
                isShowHistory = hasFocus && it.query.isEmpty() && searchHistoryState !is SearchHistoryState.Empty,
                isFocused = hasFocus
            )
        }
    }

    fun onQueryChange(query: String) {
        _textFieldState.update {
            it.copy(
                query = query,
                isShowHistory = query.isEmpty() && it.isFocused && searchHistoryState !is SearchHistoryState.Empty,
                isShowClearIc = query.isNotEmpty()
            )
        }
        searchDebounce(query)
    }

    fun onClearIcClick() {
        onQueryChange("")
        removeSearchList()
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText

        songSearchDebounce(changedText)


    }

    fun searchRequest() {
        val newSearchText = textFieldState.value.query
        if (newSearchText.isNotEmpty()) {
            searchState.update {
                SearchState.Loading
            }
            viewModelScope.launch {
                songInteractor.searchSong(newSearchText).collect { pair ->
                    processResult(pair.first, pair.second)
                }
            }
        }

    }

    fun processResult(foundSongs: List<Song>?, errorMessage: String?) {
        val songs = mutableListOf<Song>()
        if (foundSongs != null) {
            getFavouriteIds()
            foundSongs.map {
                it.apply { it.isFavourite = ids.contains(it.trackId) }
            }
            songs.addAll(foundSongs)
        }
        when {
            errorMessage != null -> {
                renderSearchState(
                    SearchState.Error(
                        message = R.string.failed_search
                    )
                )
            }

            songs.isEmpty() -> {
                renderSearchState(SearchState.Empty(R.string.nothing_found))
            }

            else -> {
                getFavouriteIds()
                renderSearchState(
                    SearchState.Content(
                        songs
                    )
                )
            }
        }
    }

    fun getFavouriteIds() {
        viewModelScope.launch {
            ids = dbSongInteractor.getFavouriteSongsIds()
        }
    }

    fun addTrack(song: Song): Int {
        var position = -1
        viewModelScope.launch {
            position = historyInteractor.saveToHistory(song)
            getHistory()
        }

        return position
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyInteractor.saveListToHistory(emptyList())
            getHistory()
        }

    }

    fun getHistory() {
        viewModelScope.launch {
            historyInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
                override fun consume(searchHistory: List<Song>?) {

                    if (searchHistory != null) {
                        if (searchHistory.isEmpty()) {
                            renderSearchHistoryState(SearchHistoryState.Empty())
                        } else {
                            renderSearchHistoryState(SearchHistoryState.Content(searchHistory))
                        }
                    } else {
                        renderSearchHistoryState(SearchHistoryState.Empty())
                    }
                }

            })
        }
    }

    fun isSavedTracksArrayListNotEmpty(): Boolean {
        return when (getSearchHistoryState().value) {
            is SearchHistoryState.Empty -> false
            else -> true
        }
    }


    init {
        getHistory()
    }

    companion object {

        private const val SEARCH_DEBOUNCE_DELAY = 3000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    fun removeSearchList() {
        renderSearchState(SearchState.Content(emptyList<Song>()))
    }

    override fun onCleared() {
        super.onCleared()

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun renderSearchState(state: SearchState) {
        searchState.update { state }
    }

    private fun renderSearchHistoryState(state: SearchHistoryState) {
        searchHistoryState.update { state }
    }


}