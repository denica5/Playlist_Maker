package com.denica.playlistmaker.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.denica.playlistmaker.App
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val DARK_THEME_MODE_KEY = "dark_theme_mode_key"

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    val viewModel by viewModel<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        binding.settingsHeader.setNavigationOnClickListener {
            finish()
        }
        binding.darkThemeSwitch.isChecked =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        binding.shareAppCategory.setOnClickListener {
            startActivity(viewModel.shareApp())
        }
        binding.writeSupportCategory.setOnClickListener {
            startActivity(viewModel.supportApp())
        }
        binding.userAgreementCategory.setOnClickListener {

            startActivity(viewModel.termsApp())

        }
        binding.darkThemeSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            viewModel.saveSettings(binding.darkThemeSwitch.isChecked)
        }

    }
}