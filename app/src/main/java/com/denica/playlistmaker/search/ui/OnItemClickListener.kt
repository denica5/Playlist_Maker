package com.denica.playlistmaker.search.ui

import com.denica.playlistmaker.search.domain.models.Song

interface OnItemClickListener {
    fun onItemClick(song: Song)
}