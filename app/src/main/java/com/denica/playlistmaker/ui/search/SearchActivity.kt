package com.denica.playlistmaker.ui.search

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denica.playlistmaker.Creator
import com.denica.playlistmaker.ui.settings.PLAYLIST_MAKER_PREFERENCES
import com.denica.playlistmaker.R
import com.denica.playlistmaker.data.dto.SongDto
import com.denica.playlistmaker.ui.search.OnItemClickListener

import com.denica.playlistmaker.domain.api.SongInteractor
import com.denica.playlistmaker.domain.models.Song
import com.denica.playlistmaker.ui.search.mediaplayer.MediaPlayerActivity
import com.google.android.material.appbar.MaterialToolbar


const val TRACK_KEY = "TRACK_KEY"

class SearchActivity : AppCompatActivity() {
    private var searchText = ""

    private val songs = arrayListOf<Song>()
    private lateinit var songInteractor: SongInteractor
    private val savedTracksArrayList = arrayListOf<Song>()
    private lateinit var adapter: TrackListAdapter
    private lateinit var historyAdapter: TrackListAdapter
    private lateinit var trackListRc: RecyclerView
    private lateinit var notFoundError: LinearLayout
    private lateinit var failedSearchError: LinearLayout
    private lateinit var searchEditText: EditText
    private lateinit var youSearchTextView: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var sharedPref: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchProgressBar: ProgressBar
    private val searchRunnable = Runnable { searchRequest() }
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchEditText = findViewById(R.id.search_edit_text)
        val searchClearIc = findViewById<ImageView>(R.id.search_clear_ic_x)
        val searchHeader = findViewById<MaterialToolbar>(R.id.search_header)
        val refreshButton = findViewById<Button>(R.id.refresh_button)
        youSearchTextView = findViewById(R.id.you_search_text_view)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        trackListRc = findViewById(R.id.track_list_rc)
        notFoundError = findViewById(R.id.not_found_error)
        failedSearchError = findViewById(R.id.failed_search_error)
        searchProgressBar = findViewById(R.id.search_progress_bar)
        sharedPref = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        val itemClickListener = object : OnItemClickListener {
            override fun onItemClick(song: Song) {
                val position = searchHistory.addTrack(savedTracksArrayList, song)
                startActivity(Intent(this@SearchActivity, MediaPlayerActivity::class.java).apply {
                    putExtra(TRACK_KEY, song)
                })

                if (position != -1) {
                    historyAdapter.notifyItemMoved(position, 0)
                    trackListRc.scrollToPosition(0)
                }
            }
        }
        songInteractor = Creator.provideSongsInteractor()

        adapter = TrackListAdapter(itemClickListener)
        historyAdapter = TrackListAdapter(itemClickListener)
        searchHistory = SearchHistory(sharedPref)
        savedTracksArrayList.addAll(searchHistory.read())
        historyAdapter.itemList = savedTracksArrayList
        searchEditText.setText(searchText)
        searchClearIc.setOnClickListener {
            searchEditText.setText("")
            try {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
                clearTracks("clear_button")
            } catch (e: Exception) {
                //
            }

        }
        refreshButton.setOnClickListener {
            callToApi()
        }


        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearIc.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
                if (searchText != "") {
                    searchDebounce()
                }

                if (savedTracksArrayList.isNotEmpty()) {
                    if (searchEditText.hasFocus() && s?.isEmpty() == true) {
                        clearHistoryButton.isVisible = true
                        youSearchTextView.isVisible = true
                        trackListRc.adapter = historyAdapter
                    } else {
                        clearHistoryButton.isVisible = false
                        youSearchTextView.isVisible = false
                        trackListRc.adapter = adapter
                    }
                } else {
                    clearHistoryButton.isVisible = false
                    youSearchTextView.isVisible = false
                    trackListRc.adapter = adapter
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            showHistory()
        }
        searchHeader.setNavigationOnClickListener {
            finish()
        }


        adapter.itemList = songs
        trackListRc.adapter = adapter
        trackListRc.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchEditText.text.isNotEmpty()) {
                    callToApi()
                } else {
                    clearTracks(getString(R.string.nothing_found))
                }
                return@setOnEditorActionListener true
            }
            false
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory(savedTracksArrayList)
            clearHistoryButton.isVisible = false
            youSearchTextView.isVisible = false

        }

    }

    private fun searchRequest() {
        callToApi()
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onStop() {
        super.onStop()
        searchHistory.write(savedTracksArrayList)
    }

    private fun callToApi() {
        searchProgressBar.isVisible = true
        trackListRc.isVisible = false
        notFoundError.isVisible = false
        failedSearchError.isVisible = false
        songInteractor.searchSong(
            searchEditText.text.toString(),
            object : SongInteractor.SongConsumer {
                override fun consume(foundSongs: Pair<List<Song>, Boolean>) {
                    if (foundSongs.second) {
                        songs.clear()
                        if (foundSongs.first.isNotEmpty()) {
                            songs.addAll(foundSongs.first)
                            handler.post() { adapter.notifyDataSetChanged() }
                        }
                        if (songs.isEmpty()) {
                            handler.post() { clearTracks(getString(R.string.nothing_found)) }

                        } else {
                            handler.post() { clearTracks("") }

                        }
                    } else {
                        handler.post() {
                            clearTracks(
                                getString(R.string.failed_search)
                            )
                        }
                    }
                }
            })

    }

    private fun clearTracks(text: String) {

        if (text.isNotEmpty()) {
            songs.clear()
            adapter.notifyDataSetChanged()
        }
        when (text) {
            getString(R.string.failed_search) -> {
                notFoundError.isVisible = false
                trackListRc.isVisible = false
                failedSearchError.isVisible = true
                searchProgressBar.isVisible = false
            }

            getString(R.string.nothing_found) -> {
                notFoundError.isVisible = true
                trackListRc.isVisible = false
                failedSearchError.isVisible = false
                searchProgressBar.isVisible = false
            }

            "" -> {
                notFoundError.isVisible = false
                trackListRc.isVisible = true
                failedSearchError.isVisible = false
                searchProgressBar.isVisible = false
            }

            "clear_button" -> {
                notFoundError.isVisible = false
                trackListRc.isVisible = true
                failedSearchError.isVisible = false
                searchProgressBar.isVisible = false
            }
        }
    }

    fun showHistory() {
        if (savedTracksArrayList.isNotEmpty()) {
            if (searchEditText.hasFocus() && searchEditText.text.isEmpty()) {
                clearHistoryButton.isVisible = true
                youSearchTextView.isVisible = true
                trackListRc.adapter = historyAdapter
            } else {
                clearHistoryButton.isVisible = false
                youSearchTextView.isVisible = false
                trackListRc.adapter = adapter
            }
        }
    }

    companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY) ?: ""
    }


}