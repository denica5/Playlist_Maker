package com.denica.playlistmaker.search.ui

import androidx.annotation.StringRes
import com.denica.playlistmaker.search.domain.models.Song

sealed interface SearchState {
    object Loading : SearchState
    object Nothing : SearchState
    data class Error(@StringRes val message: Int) : SearchState
    data class Empty(@StringRes val message: Int) : SearchState
    data class Content(val data: List<Song>) : SearchState
}