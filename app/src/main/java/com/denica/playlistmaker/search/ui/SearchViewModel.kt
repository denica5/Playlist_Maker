package com.denica.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.denica.playlistmaker.R
import com.denica.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.denica.playlistmaker.search.domain.api.SongInteractor
import com.denica.playlistmaker.search.domain.models.Song

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


    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            stateLiveData.postValue(SearchState.Loading)
        }
        songInteractor.searchSong(
            newSearchText,
            object : SongInteractor.SongConsumer {
                override fun consume(foundSongs: List<Song>?, errorMessage: String?) {
                    handler.post {
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
                }
            })
    }


    fun isSavedTracksArrayListNotEmpty(): Boolean {
        return savedTracksArrayList.value?.isNotEmpty() ?: false
    }


    fun addTrack(song: Song): Int {
        historyInteractor.saveToHistory(song)
        if (savedTracksArrayList.value?.contains(song) == true) {
            val position = savedTracksArrayList.value?.indexOf(song) ?: -1
            return position
        }
        getHistory()
        savedTracksArrayList.notifyObserver()
        return -1
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
    fun removeSearchRequest(){
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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