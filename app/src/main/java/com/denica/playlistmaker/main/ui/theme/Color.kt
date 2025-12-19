package com.denica.playlistmaker.main.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF1A1B22)
val White = Color(0xFFFFFFFF)
val Blue = Color(0xFF3772E7)
val BlueAlpha = Color(0x553772E7)
val LightGray = Color(0xFFE6E8EB)
val LightBlue = Color(0xFF9FBBF3)
val Gray = Color(0xFFAEAFB4)

val Primary = White
val OnPrimary = Black
val ColorScheme.settingsIcon: Color
    @Composable get() = if (!isSystemInDarkTheme()) {
        Gray
    } else {
        White
    }
val ColorScheme.switchSettingTrackActive: Color
    @Composable get() = if (!isSystemInDarkTheme()) {
        LightGray
    } else {
        LightBlue
    }
val ColorScheme.switchSettingThumbActive: Color
    @Composable get() = if (!isSystemInDarkTheme()) {
        Blue
    } else {
        Blue
    }

val ColorScheme.switchSettingTrackInactive: Color
    @Composable get() = if (!isSystemInDarkTheme()) {
        LightGray
    } else {
        LightBlue
    }
val ColorScheme.switchSettingThumbInactive: Color
    @Composable get() = if (!isSystemInDarkTheme()) {
        Gray
    } else {
        Gray
    }