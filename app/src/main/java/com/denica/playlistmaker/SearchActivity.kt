package com.denica.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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

class SearchActivity : AppCompatActivity() {
    private var searchText = ""
    private val retrofit = Retrofit.Builder().baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val itunesService = retrofit.create(ItunesApi::class.java)
    private val tracks = arrayListOf<Track>()
    private val adapter = TrackListAdapter()
    private lateinit var trackListRc: RecyclerView
    private lateinit var notFoundError: LinearLayout
    private lateinit var failedSearchError: LinearLayout
    private lateinit var searchEditText: EditText
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
        val refreshButton = findViewById<Button>(R.id.button_refresh)
        trackListRc = findViewById(R.id.track_list_rc)
        notFoundError = findViewById(R.id.not_found_error)
        failedSearchError = findViewById(R.id.failed_search_error)
        searchEditText.setText(searchText)
        searchClearIc.setOnClickListener {
            searchEditText.setText("")
            try {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
                tracks.clear()
                notFoundError.visibility = View.GONE
                trackListRc.visibility = View.GONE
                failedSearchError.visibility = View.GONE
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
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)

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
                    tracks.clear()
                    notFoundError.visibility = View.VISIBLE
                    trackListRc.visibility = View.GONE
                    failedSearchError.visibility = View.GONE
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun callToApi() {
        itunesService.search(searchEditText.text.toString())
            .enqueue(object : Callback<SongResponse> {
                override fun onResponse(
                    call: Call<SongResponse>, response: Response<SongResponse>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            clearTracks(getString(R.string.nothing_found))
                            notFoundError.visibility = View.VISIBLE
                            trackListRc.visibility = View.GONE
                            failedSearchError.visibility = View.GONE
                        } else {
                            clearTracks("")
                            notFoundError.visibility = View.GONE
                            trackListRc.visibility = View.VISIBLE
                            failedSearchError.visibility = View.GONE
                        }
                    } else {
                        clearTracks(
                            getString(R.string.failed_search)
                        )
//                                    Toast.makeText(this@SearchActivity, "?", Toast.LENGTH_LONG).show()
                        notFoundError.visibility = View.GONE
                        trackListRc.visibility = View.GONE
                        failedSearchError.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                    clearTracks(getString(R.string.failed_search))
                    notFoundError.visibility = View.GONE
                    trackListRc.visibility = View.GONE
                    failedSearchError.visibility = View.VISIBLE
                }

            })
    }

    private fun clearTracks(text: String) {

        if (text.isNotEmpty()) {
            tracks.clear()
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
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