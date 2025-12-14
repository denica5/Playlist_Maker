package com.denica.playlistmaker

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Fonts = FontFamily(
    Font(R.font.ys_display_medium, FontWeight.Medium),
    Font(R.font.ys_display_regular, FontWeight.Normal)

)
val AppTypography = Typography(
    bodyMedium = TextStyle(
        fontFamily = Fonts,
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium
    ),
    bodySmall = TextStyle(
        fontFamily = Fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

