package com.denica.playlistmaker.settings.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.denica.playlistmaker.App
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.ActivitySettingsBinding

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val DARK_THEME_MODE_KEY = "dark_theme_mode_key"


class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding
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
        val sharedPref = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        binding.settingsHeader.setNavigationOnClickListener {
            finish()
        }
        binding.darkThemeSwitch.isChecked =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        binding.shareAppCategory.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                putExtra(
                    Intent.EXTRA_TEXT, getString(R.string.share_text_practicum)
                )
                setType("text/plain")
                startActivity(this)
            }
        }
        binding.writeSupportCategory.setOnClickListener {
            val writeSupportIntent = Intent(Intent.ACTION_SENDTO)

            writeSupportIntent.apply {
                data = Uri.parse("mailto:")
                putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(getString(R.string.write_support_my_email_practicum))
                )
                putExtra(
                    Intent.EXTRA_SUBJECT, getString(R.string.write_support_email_subject_practicum)
                )
                putExtra(
                    Intent.EXTRA_TEXT, getString(R.string.write_support_email_body_practicum)
                )

                startActivity(this)
            }
        }
        binding.userAgreementCategory.setOnClickListener {
            val userAgreementIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_practicum)))

            startActivity(userAgreementIntent)

        }
        binding.darkThemeSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPref.edit().putBoolean(DARK_THEME_MODE_KEY, binding.darkThemeSwitch.isChecked).apply()
        }

    }
}