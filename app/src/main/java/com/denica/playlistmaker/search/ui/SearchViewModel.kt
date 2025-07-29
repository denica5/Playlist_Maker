package com.denica.playlistmaker.search.ui

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.denica.playlistmaker.R
import com.denica.playlistmaker.creator.Creator
import com.denica.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.denica.playlistmaker.search.domain.api.SongInteractor
import com.denica.playlistmaker.search.domain.models.Song

class SearchViewModel(val context: Context) : ViewModel() {

    private val songInteractor = Creator.provideSongsInteractor()
    private val handler = Handler(Looper.getMainLooper())
    val historyInteractor: SearchHistoryInteractor = Creator.provideSearchHistoryInteractor(context)
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
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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
                                        message = context.getString(R.string.failed_search)
                                    )
                                )
                            }

                            songs.isEmpty() -> {
                                renderState(SearchState.Empty(context.getString(R.string.nothing_found)))
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
        if (savedTracksArrayList.value?.contains(song) == true) {
            val position = savedTracksArrayList.value?.indexOf(song) ?: -1
            savedTracksArrayList.value?.remove(song)
            savedTracksArrayList.value?.add(0, song)
            savedTracksArrayList.notifyObserver()
            return position
        } else {
            if ((savedTracksArrayList.value?.size ?: 0) >= 10) {
                savedTracksArrayList.value?.removeAt(savedTracksArrayList.value?.lastIndex ?: 0)
                savedTracksArrayList.value?.add(0, song)
            } else {
                savedTracksArrayList.value?.add(0, song)
            }
        }
        savedTracksArrayList.notifyObserver()
        return -1
    }

    fun clearHistory() {
        savedTracksArrayList.value?.clear()
    }

    fun saveHistory() {
        savedTracksArrayList.value?.let { historyInteractor.saveToHistory(it.toList()) }
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
        fun getFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = (this[APPLICATION_KEY] as Application)
                    SearchViewModel(app)
                }
            }

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    override fun onCleared() {
        super.onCleared()
        savedTracksArrayList.value?.let { historyInteractor.saveToHistory(it.toList()) }
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}