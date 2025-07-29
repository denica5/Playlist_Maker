package com.denica.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.denica.playlistmaker.R
import com.denica.playlistmaker.mediaLibrary.ui.MediaLibraryActivity
import com.denica.playlistmaker.search.ui.SearchActivity
import com.denica.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.media_player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val searchButton = findViewById<Button>(R.id.search_button)
        val mediaButton =  findViewById<Button>(R.id.media_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        mediaButton.setOnClickListener {
            val intent = Intent(this, MediaLibraryActivity::class.java)

            startActivity(intent)
        }
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }

}