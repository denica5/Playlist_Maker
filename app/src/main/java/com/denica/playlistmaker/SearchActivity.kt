package com.denica.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {
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
        val searchBackArrow = findViewById<ImageView>(R.id.search_back_arrow)

        searchClearIc.setOnClickListener {
            searchEditText.setText("")
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearIc.visibility = clearIcVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)

        searchBackArrow.setOnClickListener{
            finish()
        }
    }

    fun clearIcVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}