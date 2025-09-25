package com.denica.playlistmaker.settings.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denica.playlistmaker.App
import com.denica.playlistmaker.utils.BindingFragment
import com.denica.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val DARK_THEME_MODE_KEY = "dark_theme_mode_key"

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val applicationContext: Context by inject()
    val viewModel by viewModel<SettingsViewModel>()
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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