package com.denica.playlistmaker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LightColors = lightColorScheme(
    primary = White,
    onPrimary = Black,
    secondary = White,
    onSecondary = Black,
    background = White,
    onBackground = LightGray,
    surface = White,
    onSurface = LightGray
)

private val DarkColors = darkColorScheme(
    primary = Black,
    onPrimary = White,
    secondary = Black,
    onSecondary = White,
    background =Black ,
    onBackground = White,
    surface = Black,
    onSurface = White
)
val LightAppColors = AppColors(
    settingsIcon = Gray,
    switchTrackInactive = LightGray,
    switchThumbInactive = Gray,
    switchTrackActive = LightGray,
    switchThumbActive = Blue,
    editTextHint = Gray,
    editTextText = Black,
    editTextCursor = Blue,
    editTextCursorBackground = BlueAlpha,
    editTextBackground = LightGray,
    bottomSheetHandle = LightGray,
    mediaPlayerIconBg = Gray,
    icSearchIcon = Gray,
    icClearSearch = Gray
)
val DarkAppColors = AppColors(
    settingsIcon = Gray,
    switchTrackInactive = LightGray,
    switchThumbInactive = Gray,
    switchTrackActive = LightGray,
    switchThumbActive = Blue,
    editTextHint = Black,
    editTextText = Black,
    editTextCursor = Blue,
    editTextCursorBackground = BlueAlpha,
    editTextBackground = White,
    bottomSheetHandle = LightGray,
    mediaPlayerIconBg = Gray,
    icSearchIcon = Black,
    icClearSearch = Black
)
val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("AppColors not provided")
}
@Composable
fun MyAppTheme(
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val appColors = if (isDarkTheme) DarkAppColors else LightAppColors
    MaterialTheme(
        colorScheme = if (isDarkTheme) DarkColors else LightColors,
        typography = AppTypography,

    ) {
        CompositionLocalProvider(
            LocalAppColors provides appColors,
            content = content
        )
    }
}