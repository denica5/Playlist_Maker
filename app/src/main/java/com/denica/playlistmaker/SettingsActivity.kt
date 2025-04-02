package com.denica.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import java.security.AccessController.getContext


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val settingsHeader = findViewById<MaterialToolbar>(R.id.settings_header)
        val shareAppLayout = findViewById<MaterialTextView>(R.id.share_app_category)
        val writeSupportLayout = findViewById<MaterialTextView>(R.id.write_support_category)
        val userAgreementLayout = findViewById<MaterialTextView>(R.id.user_agreement_category)
        val darkThemeSwitch = findViewById<SwitchMaterial>(R.id.dark_theme_switch)
        settingsHeader.setNavigationOnClickListener {
            finish()
        }
        darkThemeSwitch.isChecked = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        shareAppLayout.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                putExtra(
                    Intent.EXTRA_TEXT, getString(R.string.share_text_practicum)
                )
                setType("text/plain")
                startActivity(this)
            }
        }
        writeSupportLayout.setOnClickListener {
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
        userAgreementLayout.setOnClickListener {
            val userAgreementIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_practicum)))

            startActivity(userAgreementIntent)

        }
        darkThemeSwitch.setOnClickListener {
            if (darkThemeSwitch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }
}