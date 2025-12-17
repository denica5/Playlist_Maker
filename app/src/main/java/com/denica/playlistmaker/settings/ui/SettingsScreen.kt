package com.denica.playlistmaker.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.denica.playlistmaker.R
import com.denica.playlistmaker.main.ui.theme.settingsIcon
import com.denica.playlistmaker.main.ui.theme.switchSettingThumbActive
import com.denica.playlistmaker.main.ui.theme.switchSettingThumbInactive
import com.denica.playlistmaker.main.ui.theme.switchSettingTrackActive
import com.denica.playlistmaker.main.ui.theme.switchSettingTrackInactive

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
                    ),

                    )

            }
        )

        SettingsRow(
            title = stringResource(R.string.share_app),
            trailing = {
                Icon(
                    painterResource(R.drawable.ic_share),
                    null,
                    tint = MaterialTheme.colorScheme.settingsIcon,
                    modifier = Modifier.padding(end = 8.dp)
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
                    tint = MaterialTheme.colorScheme.settingsIcon,
                    modifier = Modifier.padding(end = 8.dp)
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
                    tint = MaterialTheme.colorScheme.settingsIcon,
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            onClick = onAgreementClick
        )
    }
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