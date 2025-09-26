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
import kotlinx.coroutines.launch

class SearchViewModel(
    private val historyInteractor: SearchHistoryInteractor,
    private val songInteractor: SongInteractor
) : ViewModel() {


    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null
    private val savedTracksArrayList = MutableLiveData<ArrayList<Song>>()
    fun getSavedTracksArrayList(): LiveData<ArrayList<Song>> = savedTracksArrayList
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
                songInteractor.searchSong(newSearchText)
                    .collect { pair ->
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
                renderState(
                    SearchState.Error(
                        message = R.string.failed_search
                    )
                )
            }

            songs.isEmpty() -> {
                renderState(SearchState.Empty(R.string.nothing_found))
            }

            else -> {
                renderState(
                    SearchState.Content(
                        songs
                    )
                )
            }
        }
    }


    fun isSavedTracksArrayListNotEmpty(): Boolean {
        return savedTracksArrayList.value?.isNotEmpty() ?: false
    }


    fun addTrack(song: Song): Int {
        historyInteractor.saveToHistory(song)
        var position = -1
        if (savedTracksArrayList.value?.contains(song) == true) {
            position = savedTracksArrayList.value?.indexOf(song) ?: -1
        }
        getHistory()
        savedTracksArrayList.notifyObserver()
        return position
    }

    fun clearHistory() {
        savedTracksArrayList.value?.clear()
        saveHistory()
    }

    fun saveHistory() {
        savedTracksArrayList.value?.let { historyInteractor.saveListToHistory(it.toList()) }
    }

    fun getHistory() {
        historyInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Song>?) {
                savedTracksArrayList.value?.clear()
                savedTracksArrayList.value?.addAll((searchHistory?.toTypedArray() ?: emptyArray()))
            }
        })
    }


    init {
        savedTracksArrayList.value = ArrayList()
        getHistory()
    }

    companion object {

        private const val SEARCH_DEBOUNCE_DELAY = 3000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    fun removeSearchList() {
        renderState(SearchState.Content(emptyList<Song>()))
    }

    override fun onCleared() {
        super.onCleared()
        savedTracksArrayList.value?.let { historyInteractor.saveListToHistory(it.toList()) }
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

}