package com.denica.playlistmaker

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val settingsIcon: Color,
    val switchTrackInactive: Color,
    val switchThumbInactive: Color,
    val switchTrackActive: Color,
    val switchThumbActive: Color,
    val editTextHint: Color,
    val editTextText: Color,
    val editTextCursor: Color,
    val editTextCursorBackground: Color,
    val editTextBackground: Color,
    val bottomSheetHandle: Color,
    val mediaPlayerIconBg: Color,
    val icSearchIcon: Color,
    val icClearSearch: Color
)