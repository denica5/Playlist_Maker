package com.denica.playlistmaker.settings.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.denica.playlistmaker.App
import com.denica.playlistmaker.MyAppTheme
import com.denica.playlistmaker.R
import com.denica.playlistmaker.settingsIcon
import com.denica.playlistmaker.switchSettingThumbActive
import com.denica.playlistmaker.switchSettingThumbInactive
import com.denica.playlistmaker.switchSettingTrackActive
import com.denica.playlistmaker.switchSettingTrackInactive
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    @Composable
    fun SettingsRow(
        title: String,
        modifier: Modifier = Modifier,
        trailing: @Composable (() -> Unit)? = null,
        onClick: (() -> Unit)? = null
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .let {
                    if (onClick != null) {
                        it.clickable(onClick = onClick)
                    } else it
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary

            )

            if (trailing != null) {
                trailing()
            }
        }
    }

    @Composable
    fun SettingsScreen(
        innerPadding: PaddingValues,
        isDarkTheme: Boolean,
        onDarkThemeChange: (Boolean) -> Unit,
        onShareClick: () -> Unit,
        onSupportClick: () -> Unit,
        onAgreementClick: () -> Unit
    ) {
        Column {
            Text(
                text = stringResource(R.string.header_settings),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )

            SettingsRow(
                title = stringResource(R.string.dark_theme),
                trailing = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onDarkThemeChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.switchSettingThumbActive,
                            checkedTrackColor = MaterialTheme.colorScheme.switchSettingTrackActive,
                            uncheckedThumbColor = MaterialTheme.colorScheme.switchSettingThumbInactive,
                            uncheckedTrackColor = MaterialTheme.colorScheme.switchSettingTrackInactive
                        )
                    )

                },
                onClick = {}
            )

            SettingsRow(
                title = stringResource(R.string.share_app),
                trailing = {
                    Icon(
                        painterResource(R.drawable.ic_share),
                        null,
                        tint = MaterialTheme.colorScheme.settingsIcon
                    )
                },
                onClick = onShareClick
            )

            SettingsRow(
                title = stringResource(R.string.write_support),
                trailing = {
                    Icon(
                        painterResource(R.drawable.ic_support),
                        null,
                        tint = MaterialTheme.colorScheme.settingsIcon
                    )
                },
                onClick = onSupportClick
            )

            SettingsRow(
                title = stringResource(R.string.user_agreement),
                trailing = {
                    Icon(
                        painterResource(R.drawable.ic_arrow_forward),
                        null,
                        tint = MaterialTheme.colorScheme.settingsIcon
                    )
                },
                onClick = onAgreementClick
            )
        }
    }

}
