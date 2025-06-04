package com.denica.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ITUNES_BASE_URL = "https://itunes.apple.com"
const val TRACK_KEY = "TRACK_KEY"

class SearchActivity : AppCompatActivity() {
    private var searchText = ""
    private val retrofit = Retrofit.Builder().baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val itunesService = retrofit.create(ItunesApi::class.java)
    private val tracks = arrayListOf<Track>()
    private val savedTracksArrayList = arrayListOf<Track>()
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
            override fun onItemClick(track: Track) {
                val position = searchHistory.addTrack(savedTracksArrayList, track)
                startActivity(Intent(this@SearchActivity, MediaPlayerActivity::class.java).apply {
                    putExtra(TRACK_KEY, track)
                })

                if (position != -1) {
                    historyAdapter.notifyItemMoved(position, 0)
                    trackListRc.scrollToPosition(0)
                }
            }
        }
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
                if (searchEditText.text.isNotEmpty()) {
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


        adapter.itemList = tracks
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
        searchProgressBar.visibility = View.VISIBLE
        trackListRc.visibility = View.GONE


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
        itunesService.search(searchEditText.text.toString())
            .enqueue(object : Callback<SongResponse> {
                override fun onResponse(
                    call: Call<SongResponse>, response: Response<SongResponse>
                ) {
                    searchProgressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            clearTracks(getString(R.string.nothing_found))

                        } else {
                            clearTracks("")

                        }
                    } else {
                        clearTracks(
                            getString(R.string.failed_search)
                        )
//                                    Toast.makeText(this@SearchActivity, "?", Toast.LENGTH_LONG).show()

                    }
                }

                override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                    searchProgressBar.visibility = View.GONE
                    clearTracks(getString(R.string.failed_search))

                }

            })
    }

    private fun clearTracks(text: String) {

        if (text.isNotEmpty()) {
            tracks.clear()
            adapter.notifyDataSetChanged()
        }
        when (text) {
            getString(R.string.failed_search) -> {
                notFoundError.isVisible = false
                trackListRc.isVisible = false
                failedSearchError.isVisible = true
            }

            getString(R.string.nothing_found) -> {
                notFoundError.isVisible = true
                trackListRc.isVisible = false
                failedSearchError.isVisible = false
            }

            "" -> {
                notFoundError.isVisible = false
                trackListRc.isVisible = true
                failedSearchError.isVisible = false
            }

            "clear_button" -> {
                notFoundError.isVisible = false
                trackListRc.isVisible = true
                failedSearchError.isVisible = false
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