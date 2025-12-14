package com.denica.playlistmaker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = White,
    onPrimary = Black,
    background = White,
    onBackground = LightGray,
    surface = White,
    onSurface = LightGray
)

private val DarkColors = darkColorScheme(
    primary = Black,
    onPrimary = White,
    background =Black ,
    onBackground = White,
    surface = Black,
    onSurface = White
)

@Composable
fun MyAppTheme(
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (isDarkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}