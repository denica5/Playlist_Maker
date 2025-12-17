package com.denica.playlistmaker.search.ui

import androidx.room.Query

data class TextFieldState(
    val query: String = "",
    val isFocused: Boolean = false,
    val isShowHistory: Boolean = false,
    val isShowClearIc: Boolean = false
)