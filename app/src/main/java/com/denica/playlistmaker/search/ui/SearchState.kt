package com.denica.playlistmaker.search.ui

import com.denica.playlistmaker.search.domain.models.Song

sealed interface SearchState {
    object Loading : SearchState
    data class Error(val message: String) : SearchState
    data class Empty(val message: String): SearchState
    data class Content(val data: List<Song>) : SearchState
}