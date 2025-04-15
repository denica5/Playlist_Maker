package com.denica.playlistmaker

import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

private const val ITUNES_BASE_URL = "https://itunes.apple.com"

class SearchActivity : AppCompatActivity() {
    private var searchText = ""
    private val retrofit = Retrofit.Builder().baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val itunesService = retrofit.create(ItunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val searchClearIc = findViewById<ImageView>(R.id.search_clear_ic_x)
        val searchHeader = findViewById<MaterialToolbar>(R.id.search_header)
        val trackListRc = findViewById<RecyclerView>(R.id.track_list_rc)
        searchEditText.setText(searchText)
        searchClearIc.setOnClickListener {
            searchEditText.setText("")
            try {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            } catch (e: Exception) {
                //
            }

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

        val adapter = TrackListAdapter(Track.test)
        trackListRc.adapter = adapter
        trackListRc.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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