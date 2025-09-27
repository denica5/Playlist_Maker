package com.denica.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.R
import com.denica.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.denica.playlistmaker.search.domain.api.SongInteractor
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.utils.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val historyInteractor: SearchHistoryInteractor,
    private val songInteractor: SongInteractor
) : ViewModel() {


    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null
    private val searchHistoryState = MutableLiveData<SearchHistoryState>()
    fun getSearchHistoryState(): LiveData<SearchHistoryState> = searchHistoryState
    private val stateLiveData = MutableLiveData<SearchState>()
    fun getState(): LiveData<SearchState> = stateLiveData

    private val songSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText

        songSearchDebounce(changedText)


    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            stateLiveData.postValue(SearchState.Loading)
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
                renderSearchState(
                    SearchState.Content(
                        songs
                    )
                )
            }
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
        viewModelScope.launch(Dispatchers.IO) {
            historyInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
                override fun consume(searchHistory: List<Song>?) {

                    if (searchHistory != null) {
                        if (searchHistory.isEmpty()) {
                            renderSearchHistoryState(SearchHistoryState.Empty("Empty"))
                        } else {
                            renderSearchHistoryState(SearchHistoryState.Content(searchHistory))
                        }
                    } else {
                        renderSearchHistoryState(SearchHistoryState.Empty("Empty"))
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
        stateLiveData.postValue(state)
    }

    private fun renderSearchHistoryState(state: SearchHistoryState) {
        searchHistoryState.postValue(state)
    }


}