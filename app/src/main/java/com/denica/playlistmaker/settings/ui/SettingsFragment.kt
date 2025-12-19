package com.denica.playlistmaker.settings.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.denica.playlistmaker.App
import com.denica.playlistmaker.main.ui.theme.MyAppTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val DARK_THEME_MODE_KEY = "dark_theme_mode_key"

class SettingsFragment : Fragment() {

    private val applicationContext: Context by inject()
    val viewModel by viewModel<SettingsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                MyAppTheme {
                    Scaffold { innerPadding ->
                        SettingsScreen(
                            innerPadding,
                            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES,
                            { checked ->
                                (applicationContext as App).switchTheme(checked)
                                viewModel.saveSettings(checked)
                            },
                            { startActivity(viewModel.shareApp()) },
                            { startActivity(viewModel.supportApp()) },
                            { startActivity(viewModel.termsApp()) }

                        )
                    }
                }
            }
        }
    }
}
