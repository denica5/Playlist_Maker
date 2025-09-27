package com.denica.playlistmaker.search.ui

import com.denica.playlistmaker.search.domain.models.Song

sealed interface SearchHistoryState {
    data class Content(val data: List<Song>) : SearchHistoryState
    data class Empty(val message: String) : SearchHistoryState
}